package com.petvission.notificacion.service;

import com.petvission.notificacion.model.CanalNotificacion;
import com.petvission.notificacion.model.CitaNotificacionLog;
import com.petvission.notificacion.model.EstadoNotificacion;
import com.petvission.notificacion.repository.CitaNotificacionLogRepository;
import com.petvission.notificacion.repository.UsuarioNotificacionPrefRepository;
import com.petvission.reserva.model.Reserva;
import com.petvission.reserva.repository.ReservaRepository;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final SpringTemplateEngine templateEngine;
    private final UsuarioNotificacionPrefRepository prefRepository;
    private final CitaNotificacionLogRepository logRepository;
    private final ReservaRepository reservaRepository;
    private final SendGrid sendGrid;

    @Value("${app.mail.from}")
    private String mailFrom;

    @Value("${petvision.frontend.url}")
    private String frontendUrl;

    // Datos planos extraídos dentro de la transacción del hilo llamador.
    // Evita pasar entidades JPA con proxies LAZY a hilos @Async.
    public record EmailReservaData(
            Long idReserva,
            Long idUsuario,
            String correoCliente,
            String nombreCliente,
            String nombreMascota,
            String especieMascota,
            String nombreVet,
            String especialidadVet,
            LocalDate fecha,
            LocalTime hora,
            String servicio,
            String motivo
    ) {
        public static EmailReservaData from(Reserva r) {
            return new EmailReservaData(
                    r.getIdReserva(),
                    r.getUsuario().getIdUsuario(),
                    r.getUsuario().getCorreo(),
                    r.getUsuario().getNombres() + " " + r.getUsuario().getApellidos(),
                    r.getMascota().getNombre(),
                    r.getMascota().getEspecie(),
                    "Dr./Dra. " + r.getVeterinario().getUsuario().getNombres()
                            + " " + r.getVeterinario().getUsuario().getApellidos(),
                    r.getVeterinario().getEspecialidad(),
                    r.getFecha(),
                    r.getHora(),
                    r.getServicio() != null ? r.getServicio().getNombre() : "Consulta General",
                    r.getMotivo() != null ? r.getMotivo() : ""
            );
        }
    }

    @Async
    public void enviarConfirmacionReserva(EmailReservaData data, String confirmationToken) {
        log.info("Async confirmación iniciada para reserva {}", data.idReserva());
        try {
            if (!preferenciaHabilitada(data.idUsuario(), "confirmacion")) {
                log.info("Confirmación bloqueada por preferencias para reserva {}", data.idReserva());
                return;
            }
            String confirmUrl = frontendUrl + "/confirmar-cita/" + data.idReserva()
                    + "?token=" + confirmationToken;
            long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), data.fecha());

            Context ctx = buildContextBase(data);
            ctx.setVariable("confirmUrl", confirmUrl);
            ctx.setVariable("diasRestantes", diasRestantes);

            String html = templateEngine.process("email/email-recordatorio", ctx);
            String error = enviarHtml(
                    data.correoCliente(),
                    "✅ Confirmación de cita — PetVision",
                    html
            );
            registrarLog(data.idReserva(), error);
        } catch (Exception e) {
            log.error("Error al procesar/enviar confirmación de reserva {}: {}", data.idReserva(), e.getMessage(), e);
        }
    }

    @Async
    public void enviarRecordatorio7Dias(EmailReservaData data) {
        log.info("Async recordatorio iniciado para reserva {}", data.idReserva());
        try {
            if (!preferenciaHabilitada(data.idUsuario(), "recordatorio7dias")) {
                log.info("Recordatorio bloqueado por preferencias para reserva {}", data.idReserva());
                return;
            }
            long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), data.fecha());
            Context ctx = buildContextBase(data);
            String html = templateEngine.process("email/email-confirmacion", ctx);
            String error = enviarHtml(
                    data.correoCliente(),
                    "🐾 Recordatorio: tu cita es en " + diasRestantes + " días — PetVision",
                    html
            );
            registrarLog(data.idReserva(), error);
        } catch (Exception e) {
            log.error("Error al procesar/enviar recordatorio de reserva {}: {}", data.idReserva(), e.getMessage(), e);
        }
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Context buildContextBase(EmailReservaData data) {
        Context ctx = new Context();
        ctx.setVariable("nombreCliente",  data.nombreCliente());
        ctx.setVariable("nombreMascota",  data.nombreMascota());
        ctx.setVariable("especieMascota", data.especieMascota());
        ctx.setVariable("nombreVet",      data.nombreVet());
        ctx.setVariable("especialidadVet",data.especialidadVet());
        ctx.setVariable("fecha",          data.fecha());
        ctx.setVariable("hora",           data.hora());
        ctx.setVariable("servicio",       data.servicio());
        ctx.setVariable("motivo",         data.motivo());
        ctx.setVariable("idReserva",      data.idReserva());
        return ctx;
    }

    private boolean preferenciaHabilitada(Long idUsuario, String tipo) {
        return prefRepository.findByUsuario_IdUsuario(idUsuario)
                .map(pref -> {
                    if (!Boolean.TRUE.equals(pref.getEmailHabilitado())) return false;
                    return switch (tipo) {
                        case "confirmacion"      -> pref.getConfirmacionReserva();
                        case "recordatorio7dias" -> pref.getRecordatorio7dias();
                        case "recordatorio1dia"  -> pref.getRecordatorio1dia();
                        case "cancelacion"       -> pref.getCancelacionReserva();
                        default                  -> true;
                    };
                })
                .orElse(true);
    }

    private String enviarHtml(String to, String subject, String htmlBody) {
        try {
            Mail mail = new Mail(
                    new Email(mailFrom),
                    subject,
                    new Email(to),
                    new Content("text/html", htmlBody)
            );

            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);

            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.info("Email enviado a {}: {} (status {})", to, subject, response.getStatusCode());
                return null;
            } else {
                String error = "SendGrid " + response.getStatusCode() + ": " + response.getBody();
                log.error("Error al enviar email a {}: {}", to, error);
                return error;
            }
        } catch (IOException e) {
            log.error("Error al enviar email a {}: {}", to, e.getMessage());
            return e.getMessage();
        }
    }

    private void registrarLog(Long idReserva, String errorMsg) {
        logRepository.save(CitaNotificacionLog.builder()
                .cita(reservaRepository.getReferenceById(idReserva))
                .canal(CanalNotificacion.EMAIL)
                .estado(errorMsg == null ? EstadoNotificacion.ENVIADO : EstadoNotificacion.ERROR)
                .fechaEnvio(LocalDateTime.now())
                .errorMessage(errorMsg)
                .build());
    }
}

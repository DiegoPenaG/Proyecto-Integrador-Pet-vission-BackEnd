package com.petvission.notificacion.service;

import com.petvission.notificacion.model.CanalNotificacion;
import com.petvission.notificacion.model.CitaNotificacionLog;
import com.petvission.notificacion.model.EstadoNotificacion;
import com.petvission.notificacion.repository.CitaNotificacionLogRepository;
import com.petvission.notificacion.repository.UsuarioNotificacionPrefRepository;
import com.petvission.reserva.model.Reserva;
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
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final SpringTemplateEngine templateEngine;
    private final UsuarioNotificacionPrefRepository prefRepository;
    private final CitaNotificacionLogRepository logRepository;
    private final SendGrid sendGrid;

    @Value("${app.mail.from}")
    private String mailFrom;

    @Value("${petvision.frontend.url}")
    private String frontendUrl;

    @Async
    public void enviarConfirmacionReserva(Reserva reserva) {
        if (!preferenciaHabilitada(reserva.getUsuario().getIdUsuario(), "confirmacion")) return;
        try {
            Context ctx = buildContextBase(reserva);
            String html = templateEngine.process("email/email-confirmacion", ctx);
            String error = enviarHtml(
                    reserva.getUsuario().getCorreo(),
                    "✅ Confirmación de cita — PetVision",
                    html
            );
            registrarLog(reserva, error);
        } catch (Exception e) {
            log.error("Error al procesar/enviar confirmación de reserva {}: {}", reserva.getIdReserva(), e.getMessage());
        }
    }

    @Async
    public void enviarRecordatorio7Dias(Reserva reserva, String confirmationToken) {
        if (!preferenciaHabilitada(reserva.getUsuario().getIdUsuario(), "recordatorio7dias")) return;

        String confirmUrl = frontendUrl + "/confirmar-cita/" + reserva.getIdReserva()
                + "?token=" + confirmationToken;

        Context ctx = buildContextBase(reserva);
        ctx.setVariable("confirmUrl", confirmUrl);
        ctx.setVariable("diasRestantes", 7);

        String html = templateEngine.process("email/email-recordatorio", ctx);
        String error = enviarHtml(
                reserva.getUsuario().getCorreo(),
                "🐾 Recordatorio: Tu cita es en 7 días — PetVision",
                html
        );
        registrarLog(reserva, error);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Context buildContextBase(Reserva reserva) {
        Context ctx = new Context();
        ctx.setVariable("nombreCliente",
                reserva.getUsuario().getNombres() + " " + reserva.getUsuario().getApellidos());
        ctx.setVariable("nombreMascota", reserva.getMascota().getNombre());
        ctx.setVariable("especieMascota", reserva.getMascota().getEspecie());
        ctx.setVariable("nombreVet",
                "Dr./Dra. " + reserva.getVeterinario().getUsuario().getNombres()
                        + " " + reserva.getVeterinario().getUsuario().getApellidos());
        ctx.setVariable("especialidadVet", reserva.getVeterinario().getEspecialidad());
        ctx.setVariable("fecha", reserva.getFecha());
        ctx.setVariable("hora", reserva.getHora());
        ctx.setVariable("servicio",
                reserva.getServicio() != null ? reserva.getServicio().getNombre() : "Consulta General");
        ctx.setVariable("motivo",
                reserva.getMotivo() != null ? reserva.getMotivo() : "");
        ctx.setVariable("idReserva", reserva.getIdReserva());
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

    private void registrarLog(Reserva reserva, String errorMsg) {
        logRepository.save(CitaNotificacionLog.builder()
                .cita(reserva)
                .canal(CanalNotificacion.EMAIL)
                .estado(errorMsg == null ? EstadoNotificacion.ENVIADO : EstadoNotificacion.ERROR)
                .fechaEnvio(LocalDateTime.now())
                .errorMessage(errorMsg)
                .build());
    }
}

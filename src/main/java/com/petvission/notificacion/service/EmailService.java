package com.petvission.notificacion.service;

import com.petvission.notificacion.model.CanalNotificacion;
import com.petvission.notificacion.model.CitaNotificacionLog;
import com.petvission.notificacion.model.EstadoNotificacion;
import com.petvission.notificacion.repository.CitaNotificacionLogRepository;
import com.petvission.notificacion.repository.UsuarioNotificacionPrefRepository;
import com.petvission.reserva.model.Reserva;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final UsuarioNotificacionPrefRepository prefRepository;
    private final CitaNotificacionLogRepository logRepository;

    @Value("${app.mail.from}")
    private String mailFrom;

    @Value("${petvision.frontend.url}")
    private String frontendUrl;

    public void enviarConfirmacionReserva(Reserva reserva) {
        if (!preferenciaHabilitada(reserva.getUsuario().getIdUsuario(), "confirmacion")) return;

        Context ctx = buildContextBase(reserva);
        String html = templateEngine.process("email/email-confirmacion", ctx);
        String error = enviarHtml(
                reserva.getUsuario().getCorreo(),
                "✅ Confirmación de cita — PetVision",
                html
        );
        registrarLog(reserva, error);
    }

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

    /** Retorna null si el envío fue exitoso, o el mensaje de error en caso de fallo. */
    private String enviarHtml(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailFrom);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Email enviado a {}: {}", to, subject);
            return null;
        } catch (Exception e) {
            // Captura MessagingException (setup MIME) y MailException (envío SMTP)
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

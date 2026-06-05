package com.petvission.notificacion.service;

import com.petvission.mascota.model.Mascota;
import com.petvission.notificacion.model.CitaNotificacionLog;
import com.petvission.notificacion.model.EstadoNotificacion;
import com.petvission.notificacion.model.UsuarioNotificacionPref;
import com.petvission.notificacion.repository.CitaNotificacionLogRepository;
import com.petvission.notificacion.repository.UsuarioNotificacionPrefRepository;
import com.petvission.reserva.model.Reserva;
import com.petvission.usuario.model.Usuario;
import com.petvission.usuario.model.UsuarioVeterinario;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock JavaMailSender mailSender;
    @Mock SpringTemplateEngine templateEngine;
    @Mock UsuarioNotificacionPrefRepository prefRepository;
    @Mock CitaNotificacionLogRepository logRepository;

    @InjectMocks
    EmailService emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "mailFrom", "noreply@petvision.com");
        ReflectionTestUtils.setField(emailService, "frontendUrl", "http://localhost:5173");
    }

    private Reserva buildReserva(Long id) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNombres("Ana");
        usuario.setApellidos("García");
        usuario.setCorreo("ana@test.com");

        Mascota mascota = new Mascota();
        mascota.setNombre("Firulais");
        mascota.setEspecie("Perro");

        Usuario vetUsuario = new Usuario();
        vetUsuario.setNombres("Carlos");
        vetUsuario.setApellidos("López");

        UsuarioVeterinario vet = new UsuarioVeterinario();
        vet.setIdUsuario(2L);
        vet.setEspecialidad("Cirugía");
        vet.setUsuario(vetUsuario);

        return Reserva.builder()
                .idReserva(id)
                .usuario(usuario)
                .mascota(mascota)
                .veterinario(vet)
                .fecha(LocalDate.now().plusDays(7))
                .hora(LocalTime.of(10, 0))
                .build();
    }

    @Test
    void enviarConfirmacion_cuandoEmailHabilitado_invocaMailSender() throws Exception {
        Reserva reserva = buildReserva(1L);
        MimeMessage mimeMock = mock(MimeMessage.class);

        when(prefRepository.findByUsuario_IdUsuario(1L)).thenReturn(Optional.empty()); // default true
        when(templateEngine.process(eq("email/email-confirmacion"), any(Context.class)))
                .thenReturn("<html>confirmacion</html>");
        when(mailSender.createMimeMessage()).thenReturn(mimeMock);

        emailService.enviarConfirmacionReserva(reserva);

        verify(mailSender).send(mimeMock);
    }

    @Test
    void noEnviarEmail_cuandoEmailHabilitadoEsFalse() {
        Reserva reserva = buildReserva(2L);

        UsuarioNotificacionPref pref = UsuarioNotificacionPref.builder()
                .emailHabilitado(false)
                .build();
        when(prefRepository.findByUsuario_IdUsuario(1L)).thenReturn(Optional.of(pref));

        emailService.enviarConfirmacionReserva(reserva);

        verifyNoInteractions(mailSender);
        verifyNoInteractions(templateEngine);
    }

    @Test
    void noEnviarRecordatorio_cuandoRecordatorio7DiasEsFalse() {
        Reserva reserva = buildReserva(3L);

        UsuarioNotificacionPref pref = UsuarioNotificacionPref.builder()
                .emailHabilitado(true)
                .recordatorio7dias(false)
                .build();
        when(prefRepository.findByUsuario_IdUsuario(1L)).thenReturn(Optional.of(pref));

        emailService.enviarRecordatorio7Dias(reserva, "token-xyz");

        verifyNoInteractions(mailSender);
    }

    @Test
    void registrarLog_estadoEnviado_cuandoEnvioExitoso() throws Exception {
        Reserva reserva = buildReserva(4L);
        MimeMessage mimeMock = mock(MimeMessage.class);

        when(prefRepository.findByUsuario_IdUsuario(1L)).thenReturn(Optional.empty());
        when(templateEngine.process(eq("email/email-confirmacion"), any(Context.class)))
                .thenReturn("<html>ok</html>");
        when(mailSender.createMimeMessage()).thenReturn(mimeMock);

        emailService.enviarConfirmacionReserva(reserva);

        ArgumentCaptor<CitaNotificacionLog> logCaptor = ArgumentCaptor.forClass(CitaNotificacionLog.class);
        verify(logRepository).save(logCaptor.capture());
        assertThat(logCaptor.getValue().getEstado()).isEqualTo(EstadoNotificacion.ENVIADO);
        assertThat(logCaptor.getValue().getErrorMessage()).isNull();
    }

    @Test
    void registrarLog_estadoError_cuandoEnvioFalla() throws Exception {
        Reserva reserva = buildReserva(5L);
        MimeMessage mimeMock = mock(MimeMessage.class);

        when(prefRepository.findByUsuario_IdUsuario(1L)).thenReturn(Optional.empty());
        when(templateEngine.process(eq("email/email-confirmacion"), any(Context.class)))
                .thenReturn("<html>ok</html>");
        when(mailSender.createMimeMessage()).thenReturn(mimeMock);
        // MimeMessageHelper.setText() fallará porque mimeMock no es un MimeMessage real
        // Simulamos el fallo directamente en send()
        doThrow(new RuntimeException("SMTP down")).when(mailSender).send(any(MimeMessage.class));

        emailService.enviarConfirmacionReserva(reserva);

        ArgumentCaptor<CitaNotificacionLog> logCaptor = ArgumentCaptor.forClass(CitaNotificacionLog.class);
        verify(logRepository).save(logCaptor.capture());
        assertThat(logCaptor.getValue().getEstado()).isEqualTo(EstadoNotificacion.ERROR);
    }

    @Test
    void enviarRecordatorio_incluyeTokenEnUrl() throws Exception {
        Reserva reserva = buildReserva(6L);
        MimeMessage mimeMock = mock(MimeMessage.class);

        when(prefRepository.findByUsuario_IdUsuario(1L)).thenReturn(Optional.empty());
        when(mailSender.createMimeMessage()).thenReturn(mimeMock);

        ArgumentCaptor<Context> ctxCaptor = ArgumentCaptor.forClass(Context.class);
        when(templateEngine.process(eq("email/email-recordatorio"), ctxCaptor.capture()))
                .thenReturn("<html>recordatorio</html>");

        emailService.enviarRecordatorio7Dias(reserva, "mi-token-secreto");

        String confirmUrl = (String) ctxCaptor.getValue().getVariable("confirmUrl");
        assertThat(confirmUrl).contains("mi-token-secreto");
        assertThat(confirmUrl).contains("/confirmar-cita/6");
    }
}

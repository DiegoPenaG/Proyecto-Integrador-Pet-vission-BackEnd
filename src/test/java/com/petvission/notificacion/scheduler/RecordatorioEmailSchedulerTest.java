package com.petvission.notificacion.scheduler;

import com.petvission.mascota.model.Mascota;
import com.petvission.notificacion.service.EmailService;
import com.petvission.recordatorio.model.Recordatorio;
import com.petvission.recordatorio.repository.RecordatorioRepository;
import com.petvission.reserva.model.EstadoReserva;
import com.petvission.reserva.model.Reserva;
import com.petvission.usuario.model.Usuario;
import com.petvission.usuario.model.UsuarioVeterinario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecordatorioEmailSchedulerTest {

    @Mock
    RecordatorioRepository recordatorioRepository;

    @Mock
    EmailService emailService;

    @InjectMocks
    RecordatorioEmailScheduler scheduler;

    private Reserva buildFullReserva(Long id) {
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
                .estado(EstadoReserva.PENDIENTE)
                .build();
    }

    @Test
    void debeEnviarEmail_cuandoExistenCitasA7Dias() {
        Reserva reserva = buildFullReserva(1L);

        Recordatorio rec = Recordatorio.builder()
                .id(10L)
                .reserva(reserva)
                .enviado(false)
                .confirmado(false)
                .confirmationToken("token-test-123")
                .build();

        LocalDate fechaEsperada = LocalDate.now().plusDays(7);
        when(recordatorioRepository.findPendientesParaFecha(eq(fechaEsperada), anyList()))
                .thenReturn(List.of(rec));

        scheduler.enviarRecordatorios7Dias();

        verify(emailService).enviarRecordatorio7Dias(any(EmailService.EmailReservaData.class));
        ArgumentCaptor<Recordatorio> captor = ArgumentCaptor.forClass(Recordatorio.class);
        verify(recordatorioRepository).save(captor.capture());
        assertThat(captor.getValue().getEnviado()).isTrue();
        assertThat(captor.getValue().getFechaEnvio()).isNotNull();
    }

    @Test
    void noDebeEnviarEmail_cuandoNoHayCitasPendientes() {
        when(recordatorioRepository.findPendientesParaFecha(any(), anyList()))
                .thenReturn(Collections.emptyList());

        scheduler.enviarRecordatorios7Dias();

        verifyNoInteractions(emailService);
        verify(recordatorioRepository, never()).save(any());
    }

    @Test
    void debeGenerarToken_cuandoRecordatorioNoTieneToken() {
        Reserva reserva = buildFullReserva(2L);

        Recordatorio rec = Recordatorio.builder()
                .id(11L)
                .reserva(reserva)
                .enviado(false)
                .confirmado(false)
                .confirmationToken(null)
                .build();

        when(recordatorioRepository.findPendientesParaFecha(any(), anyList()))
                .thenReturn(List.of(rec));

        scheduler.enviarRecordatorios7Dias();

        ArgumentCaptor<Recordatorio> captor = ArgumentCaptor.forClass(Recordatorio.class);
        verify(recordatorioRepository).save(captor.capture());
        assertThat(captor.getValue().getConfirmationToken()).isNotNull().isNotBlank();
    }

    @Test
    void debeContinuarConSiguiente_cuandoUnEnvioFalla() {
        Reserva r1 = buildFullReserva(1L);
        Reserva r2 = buildFullReserva(2L);

        Recordatorio rec1 = Recordatorio.builder().id(1L).reserva(r1).enviado(false).confirmationToken("t1").build();
        Recordatorio rec2 = Recordatorio.builder().id(2L).reserva(r2).enviado(false).confirmationToken("t2").build();

        when(recordatorioRepository.findPendientesParaFecha(any(), anyList()))
                .thenReturn(List.of(rec1, rec2));
        doThrow(new RuntimeException("SMTP error"))
                .doNothing()
                .when(emailService).enviarRecordatorio7Dias(any(EmailService.EmailReservaData.class));

        scheduler.enviarRecordatorios7Dias();

        // Ambos deben haberse intentado aunque el primero falló
        verify(emailService, times(2)).enviarRecordatorio7Dias(any(EmailService.EmailReservaData.class));
    }
}

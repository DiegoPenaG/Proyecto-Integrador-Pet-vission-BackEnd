package com.petvission.notificacion.scheduler;

import com.petvission.notificacion.service.EmailService;
import com.petvission.recordatorio.model.Recordatorio;
import com.petvission.recordatorio.repository.RecordatorioRepository;
import com.petvission.reserva.model.EstadoReserva;
import com.petvission.reserva.model.Reserva;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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

    @Test
    void debeEnviarEmail_cuandoExistenCitasA7Dias() {
        Reserva reserva = Reserva.builder()
                .idReserva(1L)
                .fecha(LocalDate.now().plusDays(7))
                .estado(EstadoReserva.PENDIENTE)
                .build();

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

        verify(emailService).enviarRecordatorio7Dias(reserva);
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
        Reserva reserva = Reserva.builder()
                .idReserva(2L)
                .fecha(LocalDate.now().plusDays(7))
                .estado(EstadoReserva.PENDIENTE)
                .build();

        Recordatorio rec = Recordatorio.builder()
                .id(11L)
                .reserva(reserva)
                .enviado(false)
                .confirmado(false)
                .confirmationToken(null) // sin token
                .build();

        when(recordatorioRepository.findPendientesParaFecha(any(), anyList()))
                .thenReturn(List.of(rec));

        scheduler.enviarRecordatorios7Dias();

        // Debe haber generado un token antes de enviar
        ArgumentCaptor<Recordatorio> captor = ArgumentCaptor.forClass(Recordatorio.class);
        verify(recordatorioRepository).save(captor.capture());
        assertThat(captor.getValue().getConfirmationToken()).isNotNull().isNotBlank();
    }

    @Test
    void debeContinuarConSiguiente_cuandoUnEnvioFalla() {
        Reserva r1 = Reserva.builder().idReserva(1L).fecha(LocalDate.now().plusDays(7)).estado(EstadoReserva.PENDIENTE).build();
        Reserva r2 = Reserva.builder().idReserva(2L).fecha(LocalDate.now().plusDays(7)).estado(EstadoReserva.PENDIENTE).build();

        Recordatorio rec1 = Recordatorio.builder().id(1L).reserva(r1).enviado(false).confirmationToken("t1").build();
        Recordatorio rec2 = Recordatorio.builder().id(2L).reserva(r2).enviado(false).confirmationToken("t2").build();

        when(recordatorioRepository.findPendientesParaFecha(any(), anyList()))
                .thenReturn(List.of(rec1, rec2));
        doThrow(new RuntimeException("SMTP error")).when(emailService).enviarRecordatorio7Dias(r1);

        scheduler.enviarRecordatorios7Dias();

        // El segundo debe haberse enviado aunque el primero falló
        verify(emailService).enviarRecordatorio7Dias(r2);
    }
}

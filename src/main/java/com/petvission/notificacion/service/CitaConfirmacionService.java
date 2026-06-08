package com.petvission.notificacion.service;

import com.petvission.recordatorio.model.Recordatorio;
import com.petvission.recordatorio.repository.RecordatorioRepository;
import com.petvission.reserva.model.EstadoReserva;
import com.petvission.reserva.model.Reserva;
import com.petvission.reserva.repository.ReservaRepository;
import com.petvission.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CitaConfirmacionService {

    private final ReservaRepository reservaRepository;
    private final RecordatorioRepository recordatorioRepository;

    @Transactional
    public void confirmarCita(Long idReserva, String token) {

        Recordatorio rec = recordatorioRepository
                .findByReserva_IdReservaAndConfirmationToken(idReserva, token)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Enlace de confirmación inválido o no pertenece a esta cita"));

        if (rec.getConfirmado()) {
            throw new IllegalStateException("La cita ya fue confirmada previamente");
        }

        Reserva reserva = rec.getReserva();

        if (reserva.getFecha().isBefore(LocalDate.now())) {
            throw new IllegalStateException(
                    "No se puede confirmar: la fecha de la cita ya pasó");
        }

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new IllegalStateException(
                    "Solo se puede confirmar una cita en estado PENDIENTE (estado actual: "
                            + reserva.getEstado() + ")");
        }

        reserva.setEstado(EstadoReserva.CONFIRMADA);
        reservaRepository.save(reserva);

        rec.setConfirmado(true);
        rec.setFechaConfirmacion(LocalDateTime.now());
        recordatorioRepository.save(rec);
    }

    @Transactional(readOnly = true)
    public EstadoReserva obtenerEstadoCita(Long idReserva) {
        return reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"))
                .getEstado();
    }
}

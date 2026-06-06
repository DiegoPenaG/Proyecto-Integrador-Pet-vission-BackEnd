package com.petvission.reserva.scheduler;

import com.petvission.reserva.model.EstadoReserva;
import com.petvission.reserva.model.Reserva;
import com.petvission.reserva.repository.ReservaRepository;
import com.petvission.turno.repository.TurnoDetalleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservaCancelScheduler {

    private final ReservaRepository reservaRepository;
    private final TurnoDetalleRepository turnoDetalleRepository;

    @Scheduled(fixedRate = 3_600_000)
    @Transactional
    public void cancelarReservasVencidas() {

        LocalDate hoy = LocalDate.now();
        LocalTime horaLimite = LocalTime.now().minusHours(1);

        List<Reserva> vencidas = reservaRepository.findReservasVencidas(
                List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA),
                hoy,
                horaLimite
        );

        if (vencidas.isEmpty()) return;

        for (Reserva reserva : vencidas) {
            reserva.setEstado(EstadoReserva.CANCELADA);
            if (reserva.getTurnoDetalle() != null) {
                reserva.getTurnoDetalle().setDisponible(true);
                turnoDetalleRepository.save(reserva.getTurnoDetalle());
            }
        }

        reservaRepository.saveAll(vencidas);
        log.info("Auto-cancel: {} reserva(s) cancelada(s) por vencimiento", vencidas.size());
    }
}

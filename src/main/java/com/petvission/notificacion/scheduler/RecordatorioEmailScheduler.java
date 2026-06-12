package com.petvission.notificacion.scheduler;

import com.petvission.notificacion.service.EmailService;
import com.petvission.recordatorio.model.Recordatorio;
import com.petvission.recordatorio.repository.RecordatorioRepository;
import com.petvission.reserva.model.EstadoReserva;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecordatorioEmailScheduler {

    private final RecordatorioRepository recordatorioRepository;
    private final EmailService emailService;

    /** Ejecuta cada día a las 9:00 AM. Busca citas a 7 días y envía recordatorio. */
    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void enviarRecordatorios7Dias() {
        LocalDate fechaObjetivo = LocalDate.now().plusDays(7);
        List<EstadoReserva> activos = List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA);

        List<Recordatorio> pendientes = recordatorioRepository.findPendientesParaFecha(fechaObjetivo, activos);
        if (pendientes.isEmpty()) return;

        int enviados = 0;
        for (Recordatorio r : pendientes) {
            try {
                if (r.getConfirmationToken() == null) {
                    r.setConfirmationToken(UUID.randomUUID().toString());
                }
                emailService.enviarRecordatorio7Dias(r.getReserva());
                r.setEnviado(true);
                r.setFechaEnvio(LocalDateTime.now());
                recordatorioRepository.save(r);
                enviados++;
            } catch (Exception e) {
                log.error("Error al procesar recordatorio id={}: {}", r.getId(), e.getMessage());
            }
        }
        log.info("Scheduler recordatorios: {}/{} enviado(s) para fecha {}", enviados, pendientes.size(), fechaObjetivo);
    }
}

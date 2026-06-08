package com.petvission.turno.scheduler;

import com.petvission.turno.service.TurnoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TurnoRollingWindowScheduler {

    private static final int VENTANA_DIAS = 60;

    private final TurnoService turnoService;

    // Corre a las 3am todos los días para mantener la ventana de 60 días llena
    @Scheduled(cron = "0 0 3 * * *")
    public void mantenerVentana() {
        log.info("TurnoRollingWindowScheduler: generando ventana de {} días...", VENTANA_DIAS);
        var resultado = turnoService.generarTurnos(VENTANA_DIAS);
        log.info("TurnoRollingWindowScheduler: {} nuevos slots generados", resultado.getTurnosGenerados());
    }
}

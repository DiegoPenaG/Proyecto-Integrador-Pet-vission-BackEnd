package com.petvission.turno;

import com.petvission.turno.model.DiaSemana;
import com.petvission.turno.model.HorarioPlantilla;
import com.petvission.turno.repository.HorarioPlantillaRepository;
import com.petvission.turno.service.TurnoService;
import com.petvission.usuario.model.UsuarioVeterinario;
import com.petvission.usuario.repository.UsuarioVeterinarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlantillaDataInitializer {

    private final HorarioPlantillaRepository plantillaRepo;
    private final UsuarioVeterinarioRepository vetRepo;
    private final TurnoService turnoService;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() {
        if (plantillaRepo.count() > 0) return;

        List<UsuarioVeterinario> vets = vetRepo.findAll();
        if (vets.isEmpty()) {
            log.warn("PlantillaDataInitializer: no hay veterinarios — se omite el seed");
            return;
        }

        log.info("Sembrando plantillas de horario para {} veterinarios...", vets.size());

        record Turno(LocalTime inicio, LocalTime fin) {}
        List<Turno> turnos = List.of(
                new Turno(LocalTime.of(8,  0), LocalTime.of(14, 0)),
                new Turno(LocalTime.of(14, 0), LocalTime.of(20, 0)),
                new Turno(LocalTime.of(20, 0), LocalTime.of(2,  0))
        );

        DiaSemana[] dias = DiaSemana.values();

        for (int i = 0; i < vets.size(); i++) {
            Turno t = turnos.get(i % turnos.size());
            for (DiaSemana dia : dias) {
                plantillaRepo.save(HorarioPlantilla.builder()
                        .veterinario(vets.get(i))
                        .diaSemana(dia)
                        .horaInicio(t.inicio())
                        .horaFin(t.fin())
                        .activo(true)
                        .build());
            }
        }

        log.info("Plantillas sembradas: {} registros", plantillaRepo.count());

        var resultado = turnoService.generarTurnos(60);
        log.info("Turnos generados al arrancar: {} slots", resultado.getTurnosGenerados());
    }
}

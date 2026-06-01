package com.petvission.turno;

import com.petvission.turno.model.DiaSemana;
import com.petvission.turno.model.HorarioPlantilla;
import com.petvission.turno.repository.HorarioPlantillaRepository;
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

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() {
        if (plantillaRepo.count() > 0) return;

        log.info("Sembrando plantillas de horario para los 6 veterinarios...");

        DiaSemana[] dias = DiaSemana.values();

        insertar(List.of(35L, 36L), dias, LocalTime.of(8, 0),  LocalTime.of(14, 0));
        insertar(List.of(37L, 38L), dias, LocalTime.of(14, 0), LocalTime.of(20, 0));
        insertar(List.of(39L, 40L), dias, LocalTime.of(20, 0), LocalTime.of(2, 0));

        log.info("Plantillas sembradas: {} registros", plantillaRepo.count());
    }

    private void insertar(List<Long> vetIds, DiaSemana[] dias, LocalTime inicio, LocalTime fin) {
        for (Long vetId : vetIds) {
            UsuarioVeterinario vet = vetRepo.findById(vetId).orElse(null);
            if (vet == null) {
                log.warn("Veterinario con id {} no encontrado — se omite", vetId);
                continue;
            }
            for (DiaSemana dia : dias) {
                plantillaRepo.save(HorarioPlantilla.builder()
                        .veterinario(vet)
                        .diaSemana(dia)
                        .horaInicio(inicio)
                        .horaFin(fin)
                        .activo(true)
                        .build());
            }
        }
    }
}

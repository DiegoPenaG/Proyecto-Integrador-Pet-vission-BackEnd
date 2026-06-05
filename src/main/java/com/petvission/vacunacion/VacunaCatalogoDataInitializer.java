package com.petvission.vacunacion;

import com.petvission.vacunacion.model.VacunaCatalogo;
import com.petvission.vacunacion.repository.VacunaCatalogoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class VacunaCatalogoDataInitializer {

    private final VacunaCatalogoRepository vacunaCatalogoRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() {
        long total = vacunaCatalogoRepository.count();
        if (total > 0) {
            log.info("VacunaCatalogoDataInitializer: tabla ya tiene {} registros — saltando seed.", total);
            return;
        }

        log.info("Sembrando catálogo de vacunas individuales...");

        vacunaCatalogoRepository.saveAll(List.of(

            /* ── CANINAS ──────────────────────────────────────────── */
            vacuna("Parvovirus Canino",
                    "Protección contra parvovirus, altamente contagioso en cachorros",
                    "2 dosis iniciales (3–4 semanas de diferencia) + refuerzo anual",
                    "CANINA"),

            vacuna("Moquillo (Distémper Canino)",
                    "Enfermedad viral grave que afecta sistema nervioso, respiratorio y digestivo",
                    "2 dosis iniciales + refuerzo anual",
                    "CANINA"),

            vacuna("Hepatitis Infecciosa Canina",
                    "Adenovirus tipo 1, afecta hígado, riñones y ojos",
                    "2 dosis iniciales + refuerzo anual",
                    "CANINA"),

            vacuna("Leptospirosis Canina",
                    "Bacteria zoonótica que afecta riñones e hígado; recomendada para perros con exposición a agua o animales silvestres",
                    "2 dosis iniciales + refuerzo anual",
                    "CANINA"),

            vacuna("Parainfluenza Canina",
                    "Componente de la tos de las perreras, infección respiratoria viral",
                    "2 dosis iniciales + refuerzo anual",
                    "CANINA"),

            vacuna("Bordetelosis (Tos de las perreras)",
                    "Bordetella bronchiseptica, recomendada para perros que van a plazas, hoteles o peluquerías",
                    "Dosis única intranasal o inyectable + refuerzo anual",
                    "CANINA"),

            vacuna("Rabia Canina",
                    "Obligatoria por ley en Chile. Previene la rabia, zoonosis mortal",
                    "Dosis única a los 3 meses + refuerzo al año + refuerzo cada 1–3 años",
                    "CANINA"),

            /* ── FELINAS ──────────────────────────────────────────── */
            vacuna("Panleucopenia Felina",
                    "Parvovirus felino, altamente contagioso y mortal en gatos no vacunados",
                    "2 dosis iniciales + refuerzo al año + cada 3 años en adultos",
                    "FELINA"),

            vacuna("Calicivirus Felino",
                    "Causa enfermedad respiratoria superior y úlceras orales",
                    "2 dosis iniciales + refuerzo al año",
                    "FELINA"),

            vacuna("Rinotraqueitis (Herpesvirus Felino)",
                    "Principal causa de enfermedad respiratoria en gatos; puede volverse crónica",
                    "2 dosis iniciales + refuerzo al año",
                    "FELINA"),

            vacuna("Leucemia Felina (FeLV)",
                    "Retrovirus inmunosupresor; recomendada para gatos que salen al exterior o conviven con otros felinos",
                    "2 dosis iniciales + refuerzo anual",
                    "FELINA"),

            vacuna("Rabia Felina",
                    "Obligatoria por ley en Chile. Previene la rabia, zoonosis mortal",
                    "Dosis única a los 3 meses + refuerzo al año + refuerzo cada 1–3 años",
                    "FELINA"),

            /* ── GENERALES (aplican a múltiples especies) ─────────── */
            vacuna("Microchip + vacuna antiparasitaria",
                    "Desparasitación interna y externa preventiva administrada junto a la visita de vacunación",
                    "Según peso y ciclo de vida del animal",
                    null)
        ));

        log.info("Catálogo de vacunas sembrado: {} vacunas", vacunaCatalogoRepository.count());
    }

    private VacunaCatalogo vacuna(String nombre, String descripcion, String dosis, String especie) {
        return VacunaCatalogo.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .dosis(dosis)
                .especie(especie)
                .build();
    }
}

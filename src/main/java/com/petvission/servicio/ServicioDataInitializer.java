package com.petvission.servicio;

import com.petvission.servicio.model.Servicio;
import com.petvission.servicio.model.TipoServicio;
import com.petvission.servicio.repository.ServicioRepository;
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
public class ServicioDataInitializer {

    private final ServicioRepository servicioRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() {
        long total = servicioRepository.count();
        if (total > 0) {
            log.info("ServicioDataInitializer: tabla ya tiene {} registros — saltando seed.", total);
            return;
        }

        log.info("Sembrando servicios...");

        servicioRepository.saveAll(List.of(

            /* ── LABORATORIO (3) ─────────────────────────────────────── */
            servicio("Laboratorio Clínico y Diagnóstico",
                    "Hemograma, bioquímica sanguínea, urianálisis, coprología, citología e inmunología",
                    TipoServicio.LABORATORIO),

            servicio("Imagenología",
                    "Radiografía digital, ecografía abdominal, torácica y ecocardiografía",
                    TipoServicio.LABORATORIO),

            servicio("Especialidades y Procedimientos Avanzados",
                    "Medicina preventiva, medicina felina, cirugía, anestesia y servicios exóticos",
                    TipoServicio.LABORATORIO),

            /* ── VACUNACIÓN CANINA (4) ───────────────────────────────── */
            servicio("Vacuna Cachorro (Puppy)",
                    "Primera protección contra parvovirus y distémper",
                    TipoServicio.VACUNACION),

            servicio("Vacuna Óctuple o Séxtuple",
                    "Refuerzo anual obligatorio contra múltiples enfermedades",
                    TipoServicio.VACUNACION),

            servicio("Vacuna Antirrábica Canina",
                    "Obligatoria por ley en Chile para perros",
                    TipoServicio.VACUNACION),

            servicio("Vacuna KC (Tos de las perreras)",
                    "Recomendada para perros que van a plazas, hoteles o peluquerías",
                    TipoServicio.VACUNACION),

            /* ── VACUNACIÓN FELINA (3) ───────────────────────────────── */
            servicio("Vacuna Triple Felina",
                    "Protección base contra panleucopenia, calicivirus y rinotraqueitis",
                    TipoServicio.VACUNACION),

            servicio("Vacuna Leucemia Felina",
                    "Para gatos que salen al exterior o conviven con otros felinos",
                    TipoServicio.VACUNACION),

            servicio("Vacuna Antirrábica Felina",
                    "Obligatoria por ley en Chile para gatos",
                    TipoServicio.VACUNACION),

            /* ── PACKS (2) ───────────────────────────────────────────── */
            servicio("Plan Cachorro / Gatito",
                    "Calendario inicial de dosis más desparasitaciones necesarias",
                    TipoServicio.VACUNACION),

            servicio("Refuerzo Anual Adulto",
                    "Vacuna anual + Antirrábica + desparasitación a precio preferencial",
                    TipoServicio.VACUNACION)
        ));

        log.info("Servicios sembrados: {}", servicioRepository.count());
    }

    private Servicio servicio(String nombre, String descripcion, TipoServicio tipo) {
        return Servicio.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .tipoServicio(tipo)
                .duracionMinutos(30)
                .precio(null)
                .activo(true)
                .build();
    }
}

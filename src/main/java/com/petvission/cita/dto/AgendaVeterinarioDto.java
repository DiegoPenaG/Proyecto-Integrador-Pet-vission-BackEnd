package com.petvission.cita.dto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
public class AgendaVeterinarioDto {

    private Long idVeterinario;

    private String nombreVeterinario;

    private String especialidad;

    /*
     * PRÓXIMOS HORARIOS DISPONIBLES
     */
    private List<HorarioDisponibleDto> horariosDisponibles;

    @Data
    @Builder
    public static class HorarioDisponibleDto {

        private LocalDate fecha;

        private LocalTime hora;
    }
}
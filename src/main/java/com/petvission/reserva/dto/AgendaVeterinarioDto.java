package com.petvission.reserva.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgendaVeterinarioDto {

    private Long idVeterinario;

    private String nombreVeterinario;

    private String especialidad;

    /*
     * HORARIOS DISPONIBLES
     */
    private List<HorarioDisponibleDto> horariosDisponibles;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HorarioDisponibleDto {

        private LocalDate fecha;

        private LocalTime hora;
    }
}
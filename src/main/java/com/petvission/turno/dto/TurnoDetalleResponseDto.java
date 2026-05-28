package com.petvission.turno.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class TurnoDetalleResponseDto {

    private Long id;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Boolean disponible;
}

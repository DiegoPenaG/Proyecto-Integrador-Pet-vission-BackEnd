package com.petvission.turno.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class TurnoDetalleRequestDto {

    @NotNull(message = "La hora de inicio del detalle es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin del detalle es obligatoria")
    private LocalTime horaFin;
}

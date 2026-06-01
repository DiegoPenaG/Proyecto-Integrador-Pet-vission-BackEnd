package com.petvission.turno.dto;

import com.petvission.turno.model.TipoTurno;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class TurnoRequestDto {

    @NotNull(message = "El id del veterinario es obligatorio")
    private Long idVeterinario;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;

    @NotNull(message = "El tipo de turno es obligatorio")
    private TipoTurno tipoTurno;

    @NotEmpty(message = "Debe incluir al menos un detalle de turno")
    @Valid
    private List<TurnoDetalleRequestDto> detalles;
}

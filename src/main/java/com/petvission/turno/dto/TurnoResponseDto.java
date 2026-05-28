package com.petvission.turno.dto;

import com.petvission.turno.model.TipoTurno;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class TurnoResponseDto {

    private Long id;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private TipoTurno tipoTurno;
    private Boolean activo;
    private Long idVeterinario;
    private String nombreVeterinario;
    private List<TurnoDetalleResponseDto> detalles;
}

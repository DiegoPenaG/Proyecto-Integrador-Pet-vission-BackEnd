package com.petvission.turno.dto;

import com.petvission.turno.model.DiaSemana;
import lombok.Data;

import java.time.LocalTime;

@Data
public class HorarioPlantillaRequestDto {
    private Long idVeterinario;
    private DiaSemana diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
}

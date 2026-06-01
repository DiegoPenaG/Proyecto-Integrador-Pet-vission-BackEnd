package com.petvission.turno.dto;

import com.petvission.turno.model.DiaSemana;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HorarioPlantillaResponseDto {
    private Long id;
    private Long idVeterinario;
    private String nombreVeterinario;
    private DiaSemana diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Boolean activo;
}

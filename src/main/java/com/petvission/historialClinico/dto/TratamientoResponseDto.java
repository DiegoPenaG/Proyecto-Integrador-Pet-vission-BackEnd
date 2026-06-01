package com.petvission.historialClinico.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TratamientoResponseDto {
    private Long idTratamiento;
    private String descripcion;
    private String duracion;
    private String indicaciones;
}

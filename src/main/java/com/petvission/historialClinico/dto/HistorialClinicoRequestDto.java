package com.petvission.historialClinico.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialClinicoRequestDto {

    private Long idMascota;

    private Long idVeterinario;

    private String diagnostico;

    private String tratamiento;

    private String observaciones;

    private BigDecimal peso;
}
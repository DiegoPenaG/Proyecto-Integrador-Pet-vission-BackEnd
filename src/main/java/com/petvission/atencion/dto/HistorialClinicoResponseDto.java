package com.petvission.atencion.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialClinicoResponseDto {

    private Long idHistorial;

    private String nombreMascota;

    private String nombreVeterinario;

    private String diagnostico;

    private String tratamiento;

    private String observaciones;

    private BigDecimal peso;

    private LocalDateTime fechaRegistro;

    private String receta;
}

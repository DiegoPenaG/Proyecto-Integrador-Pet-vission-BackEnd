package com.petvission.historialClinico.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NuevaConsultaRequestDto {

    @NotBlank(message = "El diagnóstico es obligatorio")
    private String diagnostico;

    private Long idReserva;
    private String tratamiento;
    private String indicaciones;
    private String duracion;
    private String observaciones;
    private String receta;
    private BigDecimal peso;
    private BigDecimal temperatura;
    private Integer frecuenciaCardiaca;
    private Integer frecuenciaRespiratoria;
    private Integer saturacionOxigeno;
    private VacunaDto vacuna;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VacunaDto {
        private Long idVacuna;
        private LocalDate proximaDosis;
        private String lote;
    }
}

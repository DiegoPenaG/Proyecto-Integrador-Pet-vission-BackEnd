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

    private String tratamiento;
    private String indicaciones;
    private String duracion;
    private String receta;
    private BigDecimal peso;
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

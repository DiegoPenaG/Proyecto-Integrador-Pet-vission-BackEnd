package com.petvission.mascota.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MascotaRequestDto {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La especie es obligatoria")
    private String especie;

    private String raza;
    private String sexo;
    private LocalDate fechaNacimiento;
    private String color;

    @DecimalMin(value = "0.0", inclusive = false,
            message = "El peso debe ser mayor a 0")
    private BigDecimal peso;
}
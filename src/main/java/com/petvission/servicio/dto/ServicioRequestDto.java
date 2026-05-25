package com.petvission.servicio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicioRequestDto {

    /* Nombre del servicio */
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    /* Categoría del servicio */
    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    /* Descripción del servicio */
    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    /* Duración estimada en minutos */
    @NotNull(message = "La duración en minutos es obligatoria")
    private Integer duracionMinutos;

    /* Precio del servicio */
    @NotNull(message = "El precio es obligatorio")
    private Double precio;

    /* Estado de disponibilidad del servicio */
    @NotNull(message = "El estado activo es obligatorio")
    private Boolean activo;
}

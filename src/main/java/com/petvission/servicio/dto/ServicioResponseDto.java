package com.petvission.servicio.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicioResponseDto {

    /* Identificador único del servicio */
    private Integer idServicio;

    /* Nombre del servicio */
    private String nombre;

    /* Categoría del servicio */
    private String categoria;

    /* Descripción del servicio */
    private String descripcion;

    /* Duración estimada en minutos */
    private Integer duracionMinutos;

    /* Precio del servicio */
    private Double precio;

    /* Estado de disponibilidad del servicio */
    private Boolean activo;
}

package com.petvission.servicio.dto;

import com.petvission.servicio.model.TipoServicio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicioRequestDto {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    /* Nullable — servicios de consulta general no tienen tipo. */
    private TipoServicio tipoServicio;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "La duración en minutos es obligatoria")
    private Integer duracionMinutos;

    /* Nullable — se muestra como "Consultar precio". */
    private Double precio;

    @NotNull(message = "El estado activo es obligatorio")
    private Boolean activo;
}

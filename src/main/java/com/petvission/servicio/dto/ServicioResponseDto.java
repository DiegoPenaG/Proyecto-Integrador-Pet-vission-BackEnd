package com.petvission.servicio.dto;

import com.petvission.servicio.model.TipoServicio;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicioResponseDto {

    private Integer idServicio;
    private String nombre;
    private TipoServicio tipoServicio;
    private String descripcion;
    private Integer duracionMinutos;
    private Double precio;
    private Boolean activo;
}

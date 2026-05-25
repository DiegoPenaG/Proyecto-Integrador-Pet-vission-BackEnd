package com.petvission.servicio.mapper;

import com.petvission.servicio.dto.ServicioRequestDto;
import com.petvission.servicio.dto.ServicioResponseDto;
import com.petvission.servicio.model.Servicio;
import org.springframework.stereotype.Component;

@Component
public class ServicioMapper {

    /*
     * Convierte un ServicioRequestDto a entidad Servicio
     */
    public Servicio toEntity(ServicioRequestDto dto) {
        return Servicio.builder()
                .nombre(dto.getNombre())
                .categoria(dto.getCategoria())
                .descripcion(dto.getDescripcion())
                .duracionMinutos(dto.getDuracionMinutos())
                .precio(dto.getPrecio())
                .activo(dto.getActivo())
                .build();
    }

    /*
     * Convierte una entidad Servicio a ServicioResponseDto
     */
    public ServicioResponseDto toDto(Servicio servicio) {
        return ServicioResponseDto.builder()
                .idServicio(servicio.getIdServicio())
                .nombre(servicio.getNombre())
                .categoria(servicio.getCategoria())
                .descripcion(servicio.getDescripcion())
                .duracionMinutos(servicio.getDuracionMinutos())
                .precio(servicio.getPrecio())
                .activo(servicio.getActivo())
                .build();
    }
}

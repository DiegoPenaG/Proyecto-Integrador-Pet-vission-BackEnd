package com.petvission.servicio.mapper;

import com.petvission.servicio.dto.ServicioRequestDto;
import com.petvission.servicio.dto.ServicioResponseDto;
import com.petvission.servicio.model.Servicio;
import org.springframework.stereotype.Component;

@Component
public class ServicioMapper {

    public Servicio toEntity(ServicioRequestDto dto) {
        return Servicio.builder()
                .nombre(dto.getNombre())
                .tipoServicio(dto.getTipoServicio())
                .descripcion(dto.getDescripcion())
                .duracionMinutos(dto.getDuracionMinutos())
                .precio(dto.getPrecio())
                .activo(dto.getActivo())
                .build();
    }

    public ServicioResponseDto toDto(Servicio servicio) {
        return ServicioResponseDto.builder()
                .idServicio(servicio.getIdServicio())
                .nombre(servicio.getNombre())
                .tipoServicio(servicio.getTipoServicio())
                .descripcion(servicio.getDescripcion())
                .duracionMinutos(servicio.getDuracionMinutos())
                .precio(servicio.getPrecio())
                .activo(servicio.getActivo())
                .build();
    }
}

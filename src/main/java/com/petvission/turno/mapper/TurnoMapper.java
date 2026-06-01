package com.petvission.turno.mapper;

import com.petvission.turno.dto.TurnoDetalleResponseDto;
import com.petvission.turno.dto.TurnoResponseDto;
import com.petvission.turno.model.Turno;
import com.petvission.turno.model.TurnoDetalle;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TurnoMapper {

    /*
     * ENTITY → RESPONSE DTO
     */
    public TurnoResponseDto toDto(Turno turno) {

        TurnoResponseDto dto = new TurnoResponseDto();

        dto.setId(turno.getId());
        dto.setFecha(turno.getFecha());
        dto.setHoraInicio(turno.getHoraInicio());
        dto.setHoraFin(turno.getHoraFin());
        dto.setTipoTurno(turno.getTipoTurno());
        dto.setActivo(turno.getActivo());

        dto.setIdVeterinario(
                turno.getVeterinario().getIdUsuario()
        );
        dto.setNombreVeterinario(
                turno.getVeterinario().getUsuario().getNombres()
                        + " " +
                        turno.getVeterinario().getUsuario().getApellidos()
        );

        dto.setDetalles(toDetalleDto(turno.getDetalles()));

        return dto;
    }

    /*
     * Lista de TurnoDetalle → lista de TurnoDetalleResponseDto
     */
    public List<TurnoDetalleResponseDto> toDetalleDto(List<TurnoDetalle> detalles) {

        if (detalles == null) return List.of();

        return detalles.stream()
                .map(this::toDetalleDto)
                .toList();
    }

    public TurnoDetalleResponseDto toDetalleDto(TurnoDetalle detalle) {

        TurnoDetalleResponseDto dto = new TurnoDetalleResponseDto();

        dto.setId(detalle.getId());
        dto.setHoraInicio(detalle.getHoraInicio());
        dto.setHoraFin(detalle.getHoraFin());
        dto.setDisponible(detalle.getDisponible());

        return dto;
    }
}

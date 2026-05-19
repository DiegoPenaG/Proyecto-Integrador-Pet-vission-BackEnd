package com.petvission.cita.mapper;

import com.petvission.cita.dto.CitaResponseDto;
import com.petvission.cita.model.Cita;
import org.springframework.stereotype.Component;

@Component
public class CitaMapper {

    public CitaResponseDto toDto(Cita cita) {
        CitaResponseDto dto = new CitaResponseDto();
        dto.setIdCita(cita.getIdCita());
        dto.setIdUsuario(
                cita.getUsuario().getIdUsuario()
        );
        dto.setNombreCliente(
                cita.getUsuario().getNombres() + " " +
                        cita.getUsuario().getApellidos()
        );
        dto.setIdVeterinario(
                cita.getVeterinario().getIdUsuario()
        );
        dto.setNombreVeterinario(
                cita.getVeterinario().getUsuario().getNombres() + " " +
                        cita.getVeterinario().getUsuario().getApellidos()
        );
        dto.setEspecialidadVeterinario(
                cita.getVeterinario().getEspecialidad()
        );
        dto.setFecha(cita.getFecha());
        dto.setHora(cita.getHora());
        dto.setMotivo(cita.getMotivo());
        dto.setEstado(cita.getEstado());
        return dto;
    }
}
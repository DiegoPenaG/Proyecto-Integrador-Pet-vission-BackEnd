package com.petvission.atencion.mapper;

import com.petvission.atencion.dto.HistorialClinicoResponseDto;
import com.petvission.atencion.model.HistorialClinico;

public class AtencionMapper {

    public static HistorialClinicoResponseDto toDto(
            HistorialClinico historial
    ) {

        return HistorialClinicoResponseDto.builder()
                .idHistorial(historial.getIdHistorial())
                .nombreMascota(
                        historial.getMascota().getNombre()
                )
                .nombreVeterinario(
                        historial.getVeterinario()
                                .getUsuario()
                                .getNombres()
                )
                .diagnostico(historial.getDiagnostico())
                .tratamiento(historial.getTratamiento())
                .observaciones(historial.getObservaciones())
                .peso(historial.getPeso())
                .fechaRegistro(historial.getFechaRegistro())
                .build();
    }
}
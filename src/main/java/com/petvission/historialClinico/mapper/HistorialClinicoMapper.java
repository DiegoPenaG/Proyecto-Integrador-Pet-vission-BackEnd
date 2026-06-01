package com.petvission.historialClinico.mapper;

import com.petvission.historialClinico.dto.HistorialClinicoResponseDto;
import com.petvission.historialClinico.model.HistorialClinico;

public class HistorialClinicoMapper {

    public static HistorialClinicoResponseDto toDto(
            HistorialClinico historial
    ) {
        return HistorialClinicoResponseDto.builder()
                .idHistorial(historial.getIdHistorial())
                .nombreMascota(
                        historial.getMascota().getNombre()
                )
                .nombreVeterinario(
                        historial.getVeterinario().getUsuario().getNombres()
                                + " " +
                                historial.getVeterinario().getUsuario().getApellidos()
                )
                .diagnostico(historial.getDiagnostico())
                .tratamiento(historial.getTratamiento())
                .receta(historial.getReceta())           // ← faltaba
                .observaciones(historial.getObservaciones())
                .peso(historial.getPeso())
                .fechaRegistro(historial.getFechaRegistro())
                .build();
    }
}
package com.petvission.historialClinico.mapper;

import com.petvission.historialClinico.dto.HistorialClinicoResponseDto;
import com.petvission.historialClinico.model.HistorialClinico;

public class HistorialClinicoMapper {

    public static HistorialClinicoResponseDto toDto(
            HistorialClinico historial
    ) {
        return HistorialClinicoResponseDto.builder()
                .idHistorial(historial.getIdHistorial())
                .idReserva(historial.getReserva() != null
                        ? historial.getReserva().getIdReserva() : null)
                .idVeterinario(historial.getVeterinario().getIdUsuario())
                .nombreMascota(historial.getMascota().getNombre())
                .nombreVeterinario(
                        historial.getVeterinario().getUsuario().getNombres()
                                + " " +
                                historial.getVeterinario().getUsuario().getApellidos()
                )
                .diagnostico(historial.getDiagnostico())
                .tratamiento(historial.getTratamiento())
                .receta(historial.getReceta())
                .observaciones(historial.getObservaciones())
                .peso(historial.getPeso())
                .temperatura(historial.getTemperatura())
                .frecuenciaCardiaca(historial.getFrecuenciaCardiaca())
                .frecuenciaRespiratoria(historial.getFrecuenciaRespiratoria())
                .saturacionOxigeno(historial.getSaturacionOxigeno())
                .fechaRegistro(historial.getFechaRegistro())
                .build();
    }
}
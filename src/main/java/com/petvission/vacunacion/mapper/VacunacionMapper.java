package com.petvission.vacunacion.mapper;

import com.petvission.vacunacion.dto.VacunacionResponseDto;
import com.petvission.vacunacion.model.Vacunacion;

public class VacunacionMapper {

    public static VacunacionResponseDto toDto(Vacunacion vacunacion) {

        return VacunacionResponseDto.builder()
                .idVacunacion(vacunacion.getIdVacunacion())
                .nombreMascota(
                        vacunacion.getMascota().getNombre()
                )
                .nombreVacuna(
                        vacunacion.getVacuna().getNombre()
                )
                .nombreVeterinario(
                        vacunacion.getVeterinario().getUsuario().getNombres()
                                + " " +
                                vacunacion.getVeterinario().getUsuario().getApellidos()
                )
                .fechaAplicacion(vacunacion.getFechaAplicacion())
                .proximaDosis(vacunacion.getProximaDosis())
                .lote(vacunacion.getLote())
                .build();
    }
}
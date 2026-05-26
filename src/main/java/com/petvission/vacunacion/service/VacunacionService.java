package com.petvission.vacunacion.service;

import com.petvission.atencion.model.Atencion;
import com.petvission.atencion.repository.AtencionRepository;

import com.petvission.mascota.model.Mascota;
import com.petvission.mascota.repository.MascotaRepository;

import com.petvission.shared.exception.ResourceNotFoundException;

import com.petvission.vacunacion.dto.VacunacionRequestDto;

import com.petvission.vacunacion.model.VacunaCatalogo;
import com.petvission.vacunacion.model.Vacunacion;

import com.petvission.vacunacion.repository.VacunaCatalogoRepository;
import com.petvission.vacunacion.repository.VacunacionRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VacunacionService {

    private final VacunacionRepository vacunacionRepository;

    private final VacunaCatalogoRepository vacunaCatalogoRepository;

    private final MascotaRepository mascotaRepository;

    private final AtencionRepository atencionRepository;

    /*
     * REGISTRAR VACUNACIÓN
     */
    public Vacunacion registrarVacunacion(
            VacunacionRequestDto dto
    ) {

        Mascota mascota =
                mascotaRepository.findById(dto.getIdMascota())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Mascota no encontrada"
                                )
                        );

        VacunaCatalogo vacuna =
                vacunaCatalogoRepository.findById(dto.getIdVacuna())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Vacuna no encontrada"
                                )
                        );

        Atencion atencion =
                atencionRepository.findById(dto.getIdAtencion())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Atención no encontrada"
                                )
                        );

        Vacunacion vacunacion = Vacunacion.builder()
                .mascota(mascota)
                .vacuna(vacuna)
                .atencion(atencion)
                .fechaAplicacion(dto.getFechaAplicacion())
                .proximaDosis(dto.getProximaDosis())
                .build();

        return vacunacionRepository.save(vacunacion);
    }
}
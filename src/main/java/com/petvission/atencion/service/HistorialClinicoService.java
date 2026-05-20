package com.petvission.atencion.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.petvission.atencion.dto.HistorialClinicoRequestDto;
import com.petvission.atencion.dto.HistorialClinicoResponseDto;
import com.petvission.atencion.model.HistorialClinico;
import com.petvission.atencion.repository.HistorialClinicoRepository;
import com.petvission.mascota.model.Mascota;
import com.petvission.mascota.repository.MascotaRepository;
import com.petvission.shared.exception.ResourceNotFoundException;
import com.petvission.usuario.model.UsuarioVeterinario;
import com.petvission.usuario.repository.UsuarioVeterinarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistorialClinicoService {

    private final HistorialClinicoRepository historialRepository;

    private final MascotaRepository mascotaRepository;

    private final UsuarioVeterinarioRepository veterinarioRepository;

     /*
     * REGISTRAR DIAGNÓSTICO
     */
    public HistorialClinicoResponseDto registrarDiagnostico(
        Long idHistorial,
        String diagnostico
    ) {

    HistorialClinico historial = historialRepository
            .findById(idHistorial)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Historial clínico no encontrado"
                    )
            );

    historial.setDiagnostico(diagnostico);

    HistorialClinico saved =
            historialRepository.save(historial);

    return HistorialClinicoResponseDto.builder()
            .idHistorial(saved.getIdHistorial())
            .nombreMascota(
                    saved.getMascota().getNombre()
            )
            .nombreVeterinario(
                    saved.getVeterinario()
                            .getUsuario()
                            .getNombres()
            )
            .diagnostico(saved.getDiagnostico())
            .tratamiento(saved.getTratamiento())
            .observaciones(saved.getObservaciones())
            .peso(saved.getPeso())
            .fechaRegistro(saved.getFechaRegistro())
            .build();
    }

    /*
    * REGISTRAR TRATAMIENTO
     */
    public HistorialClinicoResponseDto registrarTratamiento(
        Long idHistorial,
        String tratamiento,
        String receta
    ) {

    HistorialClinico historial = historialRepository
            .findById(idHistorial)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Historial clínico no encontrado"
                    )
            );

    historial.setTratamiento(tratamiento);
    historial.setReceta(receta);

    HistorialClinico saved =
            historialRepository.save(historial);

    return HistorialClinicoResponseDto.builder()
            .idHistorial(saved.getIdHistorial())
            .nombreMascota(
                    saved.getMascota().getNombre()
            )
            .nombreVeterinario(
                    saved.getVeterinario()
                            .getUsuario()
                            .getNombres()
            )
            .diagnostico(saved.getDiagnostico())
            .tratamiento(saved.getTratamiento())
            .receta(saved.getReceta())
            .observaciones(saved.getObservaciones())
            .peso(saved.getPeso())
            .fechaRegistro(saved.getFechaRegistro())
            .build();
    }



    /*
     * REGISTRAR OBSERVACIÓN MÉDICA
     */
    public HistorialClinicoResponseDto registrarObservacion(
            HistorialClinicoRequestDto dto
    ) {

        Mascota mascota = mascotaRepository
                .findById(dto.getIdMascota())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Mascota no encontrada"
                        )
                );

        UsuarioVeterinario veterinario =
                veterinarioRepository
                        .findById(dto.getIdVeterinario())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Veterinario no encontrado"
                                )
                        );

        HistorialClinico historial =
                HistorialClinico.builder()
                        .mascota(mascota)
                        .veterinario(veterinario)
                        .diagnostico(dto.getDiagnostico())
                        .tratamiento(dto.getTratamiento())
                        .observaciones(dto.getObservaciones())
                        .peso(dto.getPeso())
                        .fechaRegistro(LocalDateTime.now())
                        .build();

        HistorialClinico saved =
                historialRepository.save(historial);

        return HistorialClinicoResponseDto.builder()
                .idHistorial(saved.getIdHistorial())
                .nombreMascota(
                        saved.getMascota().getNombre()
                )
                .nombreVeterinario(
                        saved.getVeterinario()
                                .getUsuario()
                                .getNombres()
                )
                .diagnostico(saved.getDiagnostico())
                .tratamiento(saved.getTratamiento())
                .observaciones(saved.getObservaciones())
                .peso(saved.getPeso())
                .fechaRegistro(saved.getFechaRegistro())
                .build();
    }

    /*
     * OBTENER HISTORIAL DE MASCOTA
     */
    public List<HistorialClinicoResponseDto>
    obtenerHistorialMascota(Long idMascota) {

        return historialRepository
                .findByMascota_IdMascotaOrderByFechaRegistroDesc(
                        idMascota
                )
                .stream()
                .map(historial ->

                        HistorialClinicoResponseDto.builder()
                                .idHistorial(
                                        historial.getIdHistorial()
                                )
                                .nombreMascota(
                                        historial.getMascota()
                                                .getNombre()
                                )
                                .nombreVeterinario(
                                        historial.getVeterinario()
                                                .getUsuario()
                                                .getNombres()
                                )
                                .diagnostico(
                                        historial.getDiagnostico()
                                )
                                .tratamiento(
                                        historial.getTratamiento()
                                )
                                .observaciones(
                                        historial.getObservaciones()
                                )
                                .peso(
                                        historial.getPeso()
                                )
                                .fechaRegistro(
                                        historial.getFechaRegistro()
                                )
                                .build()

                )
                .toList();
    }
}

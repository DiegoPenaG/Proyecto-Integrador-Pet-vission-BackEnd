package com.petvission.historialClinico.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;

import com.petvission.historialClinico.dto.HistorialClinicoRequestDto;
import com.petvission.historialClinico.dto.HistorialClinicoResponseDto;
import com.petvission.historialClinico.dto.NuevaConsultaRequestDto;
import com.petvission.historialClinico.dto.TratamientoResponseDto;
import com.petvission.historialClinico.dto.VacunaEnHistorialDto;
import com.petvission.historialClinico.mapper.HistorialClinicoMapper;
import com.petvission.historialClinico.model.HistorialClinico;
import com.petvission.historialClinico.model.Tratamiento;
import com.petvission.historialClinico.repository.HistorialClinicoRepository;
import com.petvission.historialClinico.repository.TratamientoRepository;
import com.petvission.mascota.model.Mascota;
import com.petvission.mascota.repository.MascotaRepository;
import com.petvission.shared.exception.ResourceNotFoundException;
import com.petvission.usuario.model.Usuario;
import com.petvission.usuario.model.UsuarioVeterinario;
import com.petvission.usuario.repository.UsuarioVeterinarioRepository;
import com.petvission.vacunacion.model.VacunaCatalogo;
import com.petvission.vacunacion.model.Vacunacion;
import com.petvission.vacunacion.repository.VacunaCatalogoRepository;
import com.petvission.vacunacion.repository.VacunacionRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HistorialClinicoService {

    private final HistorialClinicoRepository historialRepository;
    private final TratamientoRepository tratamientoRepository;
    private final VacunacionRepository vacunacionRepository;
    private final VacunaCatalogoRepository vacunaCatalogoRepository;
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
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Historial clínico no encontrado"
                ));

        historial.setDiagnostico(diagnostico);

        return buildEnrichedDto(historialRepository.save(historial));
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
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Historial clínico no encontrado"
                ));

        historial.setTratamiento(tratamiento);
        historial.setReceta(receta);

        return buildEnrichedDto(historialRepository.save(historial));
    }

    /*
     * REGISTRAR OBSERVACIÓN MÉDICA (endpoint legacy)
     */
    public HistorialClinicoResponseDto registrarObservacion(
            HistorialClinicoRequestDto dto
    ) {
        Mascota mascota = mascotaRepository
                .findById(dto.getIdMascota())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Mascota no encontrada"
                ));

        UsuarioVeterinario veterinario = veterinarioRepository
                .findById(dto.getIdVeterinario())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Veterinario no encontrado"
                ));

        HistorialClinico historial = HistorialClinico.builder()
                .mascota(mascota)
                .veterinario(veterinario)
                .diagnostico(dto.getDiagnostico())
                .tratamiento(dto.getTratamiento())
                .observaciones(dto.getObservaciones() != null ? dto.getObservaciones() : "")
                .peso(dto.getPeso())
                .build();

        return buildEnrichedDto(historialRepository.save(historial));
    }

    /*
     * CREAR CONSULTA COMPLETA (idVet extraído del JWT)
     */
    @Transactional
    public HistorialClinicoResponseDto crearConsulta(
            Long idMascota,
            NuevaConsultaRequestDto dto
    ) {
        Mascota mascota = mascotaRepository
                .findById(idMascota)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Mascota no encontrada"
                ));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario currentUser = (Usuario) auth.getPrincipal();
        UsuarioVeterinario veterinario = veterinarioRepository
                .findByUsuario_IdUsuario(currentUser.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Veterinario no encontrado"
                ));

        HistorialClinico historial = HistorialClinico.builder()
                .mascota(mascota)
                .veterinario(veterinario)
                .diagnostico(dto.getDiagnostico())
                .tratamiento(dto.getTratamiento())
                .observaciones(dto.getObservaciones() != null ? dto.getObservaciones() : "")
                .receta(dto.getReceta())
                .peso(dto.getPeso())
                .build();

        historial = historialRepository.save(historial);

        if (dto.getTratamiento() != null && !dto.getTratamiento().isBlank()) {
            Tratamiento tratamiento = Tratamiento.builder()
                    .historialClinico(historial)
                    .descripcion(dto.getTratamiento())
                    .duracion(dto.getDuracion())
                    .indicaciones(dto.getIndicaciones())
                    .build();
            tratamientoRepository.save(tratamiento);
        }

        if (dto.getVacuna() != null) {
            VacunaCatalogo vacuna = vacunaCatalogoRepository
                    .findById(dto.getVacuna().getIdVacuna())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Vacuna no encontrada"
                    ));

            Vacunacion vacunacion = Vacunacion.builder()
                    .mascota(mascota)
                    .vacuna(vacuna)
                    .veterinario(veterinario)
                    .historialClinico(historial)
                    .fechaAplicacion(LocalDate.now())
                    .proximaDosis(dto.getVacuna().getProximaDosis())
                    .lote(dto.getVacuna().getLote())
                    .build();
            vacunacionRepository.save(vacunacion);
        }

        return buildEnrichedDto(historial);
    }

    /*
     * OBTENER HISTORIAL DE MASCOTA (con tratamientos y vacunas)
     * CLIENTE: solo puede ver historial de sus propias mascotas.
     */
    @Transactional(readOnly = true)
    public List<HistorialClinicoResponseDto> obtenerHistorialMascota(
            Long idMascota
    ) {
        Mascota mascota = mascotaRepository
                .findById(idMascota)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario currentUser = (Usuario) auth.getPrincipal();
        boolean isCliente = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"));

        if (isCliente && !mascota.getUsuario().getIdUsuario().equals(currentUser.getIdUsuario())) {
            throw new AccessDeniedException("No tienes permiso para ver el historial de esta mascota");
        }

        return historialRepository
                .findByMascota_IdMascotaOrderByFechaRegistroDesc(idMascota)
                .stream()
                .map(this::buildEnrichedDto)
                .toList();
    }

    /*
     * CONSTRUYE DTO ENRIQUECIDO CON TRATAMIENTOS Y VACUNAS
     */
    private HistorialClinicoResponseDto buildEnrichedDto(HistorialClinico historial) {
        HistorialClinicoResponseDto dto = HistorialClinicoMapper.toDto(historial);

        List<TratamientoResponseDto> tratamientos = tratamientoRepository
                .findByHistorialClinico_IdHistorial(historial.getIdHistorial())
                .stream()
                .map(t -> TratamientoResponseDto.builder()
                        .idTratamiento(t.getIdTratamiento())
                        .descripcion(t.getDescripcion())
                        .duracion(t.getDuracion())
                        .indicaciones(t.getIndicaciones())
                        .build()
                )
                .toList();

        List<VacunaEnHistorialDto> vacunas = vacunacionRepository
                .findByHistorialClinico_IdHistorial(historial.getIdHistorial())
                .stream()
                .map(v -> VacunaEnHistorialDto.builder()
                        .idVacunacion(v.getIdVacunacion())
                        .nombreVacuna(v.getVacuna().getNombre())
                        .fechaAplicacion(v.getFechaAplicacion())
                        .proximaDosis(v.getProximaDosis())
                        .lote(v.getLote())
                        .build()
                )
                .toList();

        dto.setTratamientos(tratamientos);
        dto.setVacunas(vacunas);
        return dto;
    }
}

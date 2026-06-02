package com.petvission.vacunacion.service;

import com.petvission.historialClinico.model.HistorialClinico;
import com.petvission.historialClinico.repository.HistorialClinicoRepository;
import com.petvission.mascota.model.Mascota;
import com.petvission.mascota.repository.MascotaRepository;
import com.petvission.shared.exception.ResourceNotFoundException;
import com.petvission.usuario.model.UsuarioVeterinario;
import com.petvission.usuario.repository.UsuarioVeterinarioRepository;
import com.petvission.usuario.model.Usuario;
import com.petvission.usuario.model.UsuarioVeterinario;
import com.petvission.usuario.repository.UsuarioVeterinarioRepository;
import com.petvission.vacunacion.dto.VacunacionDesdeConsultaDto;
import com.petvission.vacunacion.dto.VacunacionRequestDto;
import com.petvission.vacunacion.dto.VacunacionResponseDto;
import com.petvission.vacunacion.mapper.VacunacionMapper;
import com.petvission.vacunacion.model.VacunaCatalogo;
import com.petvission.vacunacion.model.Vacunacion;
import com.petvission.vacunacion.repository.VacunaCatalogoRepository;
import com.petvission.vacunacion.repository.VacunacionRepository;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VacunacionService {

    private final VacunacionRepository vacunacionRepository;
    private final VacunaCatalogoRepository vacunaCatalogoRepository;
    private final MascotaRepository mascotaRepository;
    private final HistorialClinicoRepository historialClinicoRepository;
    private final UsuarioVeterinarioRepository usuarioVeterinarioRepository;

    /*
     * REGISTRAR VACUNACIÓN
     */
    public VacunacionResponseDto registrarVacunacion(VacunacionRequestDto dto) {

        Mascota mascota = mascotaRepository.findById(dto.getIdMascota())
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada"));

        VacunaCatalogo vacuna = vacunaCatalogoRepository.findById(dto.getIdVacuna())
                .orElseThrow(() -> new ResourceNotFoundException("Vacuna no encontrada"));

        HistorialClinico historialClinico = historialClinicoRepository
                .findById(dto.getIdHistorialClinico())
                .orElseThrow(() -> new ResourceNotFoundException("Historial clínico no encontrado"));

        UsuarioVeterinario veterinario = usuarioVeterinarioRepository
                .findById(dto.getIdVeterinario())
                .orElseThrow(() -> new ResourceNotFoundException("Veterinario no encontrado"));


        Vacunacion vacunacion = Vacunacion.builder()
                .mascota(mascota)
                .vacuna(vacuna)
                .veterinario(veterinario)
                .historialClinico(historialClinico)
                .fechaAplicacion(dto.getFechaAplicacion())
                .proximaDosis(dto.getProximaDosis())
                .lote(dto.getLote())
                .build();

        Vacunacion guardada = vacunacionRepository.save(vacunacion);

        /*
         * MAPEO A RESPONSE DTO
         */
        return VacunacionMapper.toDto(vacunacionRepository.save(vacunacion));
    }

    /*
     * REGISTRAR VACUNACIÓN DESDE CONSULTA (vet extraído del JWT)
     */
    public VacunacionResponseDto registrarDesdeConsulta(
            VacunacionDesdeConsultaDto dto,
            Authentication auth
    ) {
        Usuario currentUser = (Usuario) auth.getPrincipal();

        Mascota mascota = mascotaRepository.findById(dto.getIdMascota())
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada"));

        VacunaCatalogo vacuna = vacunaCatalogoRepository.findById(dto.getIdVacuna())
                .orElseThrow(() -> new ResourceNotFoundException("Vacuna no encontrada"));

        HistorialClinico historial = historialClinicoRepository.findById(dto.getIdHistorial())
                .orElseThrow(() -> new ResourceNotFoundException("Historial clínico no encontrado"));

        UsuarioVeterinario veterinario = usuarioVeterinarioRepository
                .findByUsuario_IdUsuario(currentUser.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Veterinario no encontrado"));

        Vacunacion vacunacion = Vacunacion.builder()
                .mascota(mascota)
                .vacuna(vacuna)
                .veterinario(veterinario)
                .historialClinico(historial)
                .fechaAplicacion(dto.getFechaAplicacion() != null
                        ? dto.getFechaAplicacion() : LocalDate.now())
                .proximaDosis(dto.getFechaProxima())
                .lote(dto.getLote())
                .build();

        return VacunacionMapper.toDto(vacunacionRepository.save(vacunacion));
    }

    /*
     * CATÁLOGO DE VACUNAS
     */
    public List<VacunaCatalogo> obtenerCatalogo() {
        return vacunaCatalogoRepository.findAll();
    }
}

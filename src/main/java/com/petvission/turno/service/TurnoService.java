package com.petvission.turno.service;

import com.petvission.shared.exception.ResourceNotFoundException;
import com.petvission.turno.dto.TurnoDetalleResponseDto;
import com.petvission.turno.dto.TurnoRequestDto;
import com.petvission.turno.dto.TurnoResponseDto;
import com.petvission.turno.mapper.TurnoMapper;
import com.petvission.turno.model.Turno;
import com.petvission.turno.model.TurnoDetalle;
import com.petvission.turno.repository.TurnoDetalleRepository;
import com.petvission.turno.repository.TurnoRepository;
import com.petvission.usuario.model.UsuarioVeterinario;
import com.petvission.usuario.repository.UsuarioVeterinarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TurnoService {

    private final TurnoRepository turnoRepository;
    private final TurnoDetalleRepository turnoDetalleRepository;
    private final UsuarioVeterinarioRepository veterinarioRepository;
    private final TurnoMapper turnoMapper;

    /*
     * LISTAR TODOS LOS TURNOS
     */
    public List<TurnoResponseDto> listarTodos() {
        return turnoRepository.findAll()
                .stream()
                .map(turnoMapper::toDto)
                .toList();
    }

    /*
     * LISTAR TURNOS POR VETERINARIO
     */
    public List<TurnoResponseDto> listarPorVeterinario(Long idVeterinario) {
        return turnoRepository.findByVeterinario_IdUsuario(idVeterinario)
                .stream()
                .map(turnoMapper::toDto)
                .toList();
    }

    /*
     * LISTAR DETALLES DISPONIBLES DE UN TURNO
     */
    public List<TurnoDetalleResponseDto> listarDetallesDisponibles(Long idTurno) {
        return turnoDetalleRepository.findByTurno_IdAndDisponibleTrue(idTurno)
                .stream()
                .map(turnoMapper::toDetalleDto)
                .toList();
    }

    /*
     * DISPONIBILIDAD POR VETERINARIO
     * Retorna todos los TurnoDetalle disponibles de un veterinario
     * incluyendo la fecha del turno padre
     */
    public List<TurnoDetalleResponseDto> obtenerDisponibilidadVeterinario(Long idVeterinario) {
        return turnoDetalleRepository
                .findByTurno_Veterinario_IdUsuarioAndDisponibleTrue(idVeterinario)
                .stream()
                .map(td -> {
                    TurnoDetalleResponseDto dto = turnoMapper.toDetalleDto(td);
                    dto.setFecha(td.getTurno().getFecha());
                    return dto;
                })
                .toList();
    }

    /*
     * CREAR TURNO CON SUS DETALLES
     */
    @Transactional
    public TurnoResponseDto crear(TurnoRequestDto dto) {
        UsuarioVeterinario veterinario = veterinarioRepository
                .findById(dto.getIdVeterinario())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Veterinario no encontrado: " + dto.getIdVeterinario())
                );

        Turno turno = Turno.builder()
                .veterinario(veterinario)
                .fecha(dto.getFecha())
                .horaInicio(dto.getHoraInicio())
                .horaFin(dto.getHoraFin())
                .tipoTurno(dto.getTipoTurno())
                .activo(true)
                .build();

        Turno turnoGuardado = turnoRepository.save(turno);

        List<TurnoDetalle> detalles = dto.getDetalles().stream()
                .map(detalleDto -> TurnoDetalle.builder()
                        .turno(turnoGuardado)
                        .horaInicio(detalleDto.getHoraInicio())
                        .horaFin(detalleDto.getHoraFin())
                        .disponible(true)
                        .build()
                )
                .toList();

        turnoGuardado.setDetalles(turnoDetalleRepository.saveAll(detalles));
        return turnoMapper.toDto(turnoGuardado);
    }

    /*
     * ACTIVAR TURNO
     */
    public TurnoResponseDto activar(Long id) {
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Turno no encontrado: " + id)
                );
        turno.setActivo(true);
        return turnoMapper.toDto(turnoRepository.save(turno));
    }

    /*
     * DESACTIVAR TURNO
     */
    public TurnoResponseDto desactivar(Long id) {
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Turno no encontrado: " + id)
                );
        turno.setActivo(false);
        return turnoMapper.toDto(turnoRepository.save(turno));
    }
}

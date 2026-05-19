package com.petvission.mascota.service;

import com.petvission.mascota.dto.MascotaRequestDto;
import com.petvission.mascota.dto.MascotaResponseDto;
import com.petvission.mascota.mapper.MascotaMapper;
import com.petvission.mascota.model.Mascota;
import com.petvission.mascota.repository.MascotaRepository;
import com.petvission.shared.exception.ResourceNotFoundException;
import com.petvission.usuario.model.Usuario;
import com.petvission.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/*
 * SERVICIO DE MASCOTAS
 * CONTIENE LA LÓGICA DE NEGOCIO
 * RELACIONADA CON LA GESTIÓN
 * Y ADMINISTRACIÓN DE MASCOTAS.
 */

@Service
@RequiredArgsConstructor
public class MascotaService {

    /*
     * INYECCIÓN DE REPOSITORIOS Y MAPPER
     */
    private final MascotaRepository mascotaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MascotaMapper mascotaMapper;

    /*
     * MÉTODO PARA LISTAR LAS MASCOTAS
     * ASOCIADAS A UN USUARIO
     */
    public List<MascotaResponseDto> listarPorUsuario(Long idUsuario) {

        return mascotaRepository
                .findByUsuario_IdUsuario(idUsuario)
                .stream()
                .map(mascotaMapper::toDto)
                .collect(Collectors.toList());
    }

    /*
     * MÉTODO PARA REGISTRAR
     * UNA NUEVA MASCOTA
     */
    public MascotaResponseDto crear(
            Long idUsuario,
            MascotaRequestDto dto) {

        /*
         * VALIDACIÓN DE EXISTENCIA DEL USUARIO
         */
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuario no encontrado: " + idUsuario
                        )
                );

        /*
         * CONVERSIÓN DE DTO A ENTIDAD
         */
        Mascota mascota = mascotaMapper.toEntity(dto);

        /*
         * ASIGNACIÓN DEL USUARIO A LA MASCOTA
         */
        mascota.setUsuario(usuario);

        /*
         * GUARDADO DE LA MASCOTA
         */
        return mascotaMapper.toDto(
                mascotaRepository.save(mascota)
        );
    }

    /*
     * MÉTODO PARA ACTUALIZAR
     * LA INFORMACIÓN DE UNA MASCOTA
     */
    public MascotaResponseDto actualizar(
            Long id,
            MascotaRequestDto dto) {

        /*
         * VALIDACIÓN DE EXISTENCIA DE LA MASCOTA
         */

        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Mascota no encontrada"
                        )
                );

        /*
         * ACTUALIZACIÓN DE DATOS
         */
        mascota.setNombre(dto.getNombre());
        mascota.setEspecie(dto.getEspecie());
        mascota.setRaza(dto.getRaza());
        mascota.setSexo(dto.getSexo());
        mascota.setFechaNacimiento(dto.getFechaNacimiento());
        mascota.setColor(dto.getColor());
        mascota.setPeso(dto.getPeso());

        /*
         * GUARDADO DE CAMBIOS
         */
        return mascotaMapper.toDto(
                mascotaRepository.save(mascota)
        );
    }

    /*
     * MÉTODO PARA ELIMINAR UNA MASCOTA
     * MEDIANTE ELIMINACIÓN LÓGICA
     */
    public void eliminar(Long id) {

        /*
         * VALIDACIÓN DE EXISTENCIA DE LA MASCOTA
         */
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Mascota no encontrada"
                        )
                );

        /*
         * CAMBIO DE ESTADO A INACTIVO
         */
        mascota.setEstado(false);

        /*
         * GUARDADO DE CAMBIOS
         */
        mascotaRepository.save(mascota);
    }
}
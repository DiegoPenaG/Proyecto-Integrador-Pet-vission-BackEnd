package com.petvission.usuario.service;

import com.petvission.shared.exception.ResourceNotFoundException;
import com.petvission.usuario.dto.UsuarioRequestDto;
import com.petvission.usuario.dto.UsuarioResponseDto;
import com.petvission.usuario.mapper.UsuarioMapper;
import com.petvission.usuario.model.Rol;
import com.petvission.usuario.model.Usuario;
import com.petvission.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    // Listar usuarios activos
    public List<UsuarioResponseDto> listarActivos() {
        return usuarioRepository.findByEstadoTrue()
                .stream()
                .map(usuarioMapper::toDto)
                .collect(Collectors.toList());
    }

    // Obtener usuario por ID
    public UsuarioResponseDto obtenerPorId(Long id) {
        Usuario usuario = buscarOFallar(id);
        return usuarioMapper.toDto(usuario);
    }

    // Listar veterinarios activos
    public List<UsuarioResponseDto> listarVeterinarios() {
        return usuarioRepository
                .findByRol_NombreRol(Rol.NombreRol.VETERINARIO)
                .stream()
                .filter(Usuario::getEstado)
                .map(usuarioMapper::toDto)
                .collect(Collectors.toList());
    }

    // Actualizar datos básicos
    public UsuarioResponseDto actualizar(Long id, UsuarioRequestDto dto) {
        Usuario usuario = buscarOFallar(id);
        usuario.setNombres(dto.getNombres());
        usuario.setApellidos(dto.getApellidos());
        usuario.setTelefono(dto.getTelefono());
        return usuarioMapper.toDto(usuarioRepository.save(usuario));
    }

    // Soft delete
    public void desactivar(Long id) {
        Usuario usuario = buscarOFallar(id);
        usuario.setEstado(false);
        usuarioRepository.save(usuario);
    }

    // Método privado reutilizable
    private Usuario buscarOFallar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado con id: " + id
                ));
    }
}
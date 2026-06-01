package com.petvission.usuario.service;

import com.petvission.shared.exception.ResourceNotFoundException;
import com.petvission.usuario.dto.CreateVeterinarioDto;
import com.petvission.usuario.dto.UsuarioRequestDto;
import com.petvission.usuario.dto.UsuarioResponseDto;
import com.petvission.usuario.mapper.UsuarioMapper;
import com.petvission.usuario.model.Rol;
import com.petvission.usuario.model.Usuario;
import com.petvission.usuario.model.UsuarioVeterinario;
import com.petvission.usuario.repository.RolRepository;
import com.petvission.usuario.repository.UsuarioRepository;
import com.petvission.usuario.repository.UsuarioVeterinarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;
    private final UsuarioVeterinarioRepository usuarioVeterinarioRepository;

    public List<UsuarioResponseDto> listarActivos() {
        return usuarioRepository.findByEstadoTrue()
                .stream()
                .map(usuarioMapper::toDto)
                .collect(Collectors.toList());
    }

    public UsuarioResponseDto obtenerPorId(Long id) {
        return usuarioMapper.toDto(buscarOFallar(id));
    }

    public List<UsuarioResponseDto> listarVeterinarios() {
        return usuarioRepository
                .findByRol_NombreRol(Rol.NombreRol.VETERINARIO)
                .stream()
                .filter(Usuario::getEstado)
                .map(usuarioMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<UsuarioResponseDto> listarClientes() {
        return usuarioRepository
                .findByRol_NombreRol(Rol.NombreRol.CLIENTE)
                .stream()
                .filter(Usuario::getEstado)
                .map(usuarioMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UsuarioResponseDto crearVeterinario(CreateVeterinarioDto dto) {
        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }

        Rol rol = rolRepository.findByNombreRol(Rol.NombreRol.VETERINARIO)
                .orElseThrow(() -> new ResourceNotFoundException("Rol VETERINARIO no encontrado"));

        Usuario usuario = Usuario.builder()
                .nombres(dto.getNombres())
                .apellidos(dto.getApellidos())
                .correo(dto.getCorreo())
                .contrasena(passwordEncoder.encode(dto.getContrasena()))
                .telefono(dto.getTelefono())
                .rol(rol)
                .estado(true)
                .build();

        usuarioRepository.save(usuario);

        UsuarioVeterinario datosVet = UsuarioVeterinario.builder()
                .idUsuario(usuario.getIdUsuario())
                .usuario(usuario)
                .especialidad(dto.getEspecialidad() != null ? dto.getEspecialidad() : "General")
                .build();

        usuarioVeterinarioRepository.save(datosVet);

        return usuarioMapper.toDto(usuario);
    }

    public UsuarioResponseDto actualizar(Long id, UsuarioRequestDto dto) {
        Usuario usuario = buscarOFallar(id);
        usuario.setNombres(dto.getNombres());
        usuario.setApellidos(dto.getApellidos());
        usuario.setTelefono(dto.getTelefono());
        return usuarioMapper.toDto(usuarioRepository.save(usuario));
    }

    public void desactivar(Long id) {
        Usuario usuario = buscarOFallar(id);
        usuario.setEstado(false);
        usuarioRepository.save(usuario);
    }

    private Usuario buscarOFallar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado con id: " + id
                ));
    }
}

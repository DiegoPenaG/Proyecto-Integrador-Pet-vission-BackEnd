// src/main/java/com/petvission/auth/service/AuthService.java

package com.petvission.auth.service;

import com.petvission.auth.dto.AuthRequestDto;
import com.petvission.auth.dto.AuthResponseDto;
import com.petvission.auth.dto.RegisterRequestDto;
import com.petvission.usuario.model.Rol;
import com.petvission.usuario.repository.RolRepository;
import com.petvission.security.service.JwtService;
import com.petvission.shared.exception.ResourceNotFoundException;
import com.petvission.usuario.model.Usuario;
import com.petvission.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository    usuarioRepository;
    private final RolRepository        rolRepository;
    private final PasswordEncoder      passwordEncoder;
    private final JwtService           jwtService;
    private final AuthenticationManager authenticationManager;

    // ============================================
    // REGISTRO
    // ============================================
    public AuthResponseDto register(RegisterRequestDto dto) {

        // Verificar si el correo ya existe
        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }

        // Buscar el rol en la BD
        Rol rol = rolRepository.findByNombreRol(dto.getRol())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rol no encontrado: " + dto.getRol()
                ));

        // Crear el usuario
        Usuario usuario = Usuario.builder()
                .nombres(dto.getNombres())
                .apellidos(dto.getApellidos())
                .correo(dto.getCorreo())
                .contrasena(passwordEncoder.encode(dto.getPassword()))
                .telefono(dto.getTelefono())
                .rol(rol)
                .estado(true)
                .build();

        usuarioRepository.save(usuario);

        // Generar token JWT
        String token = jwtService.generarToken(usuario);

        return AuthResponseDto.builder()
                .token(token)
                .tipo("Bearer")
                .idUsuario(usuario.getIdUsuario())
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .correo(usuario.getCorreo())
                .rol(usuario.getRol().getNombreRol())
                .build();
    }

    // ============================================
    // LOGIN
    // ============================================
    public AuthResponseDto login(AuthRequestDto dto) {

        // Spring Security valida correo y contraseña
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getCorreo(),
                        dto.getPassword()
                )
        );

        // Buscar el usuario autenticado
        Usuario usuario = usuarioRepository.findByCorreo(dto.getCorreo())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado"
                ));

        // Generar token JWT
        String token = jwtService.generarToken(usuario);

        return AuthResponseDto.builder()
                .token(token)
                .tipo("Bearer")
                .idUsuario(usuario.getIdUsuario())
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .correo(usuario.getCorreo())
                .rol(usuario.getRol().getNombreRol())
                .build();
    }
}
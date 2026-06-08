// src/main/java/com/petvission/auth/service/AuthService.java

package com.petvission.auth.service;

import com.petvission.auth.dto.AuthRequestDto;
import com.petvission.auth.dto.AuthResponseDto;
import com.petvission.auth.dto.GoogleAuthRequestDto;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import com.petvission.usuario.model.UsuarioVeterinario;
import com.petvission.usuario.repository.UsuarioVeterinarioRepository;

import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository    usuarioRepository;
    private final RolRepository        rolRepository;
    private final PasswordEncoder      passwordEncoder;
    private final JwtService           jwtService;
    private final AuthenticationManager authenticationManager;
    private final UsuarioVeterinarioRepository usuarioVeterinarioRepository;
    private final RestTemplate         restTemplate = new RestTemplate();

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

        // Si el rol es VETERINARIO, crear el registro en usuario_veterinario
        if (rol.getNombreRol().name().equals("VETERINARIO")) {
            UsuarioVeterinario veterinario = UsuarioVeterinario.builder()
                    .idUsuario(usuario.getIdUsuario())
                    .usuario(usuario)
                    .especialidad("General")
                    .build();
            usuarioVeterinarioRepository.save(veterinario);
        }
        
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
    // LOGIN CON GOOGLE
    // ============================================
    @Transactional
    @SuppressWarnings("unchecked")
    public AuthResponseDto loginConGoogle(GoogleAuthRequestDto dto) {

        // Verificar el access_token con la API de Google
        String url = "https://www.googleapis.com/oauth2/v3/userinfo?access_token=" + dto.getToken();
        Map<String, Object> info;
        try {
            info = restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Token de Google inválido o expirado");
        }

        if (info == null || info.get("email") == null) {
            throw new IllegalArgumentException("No se pudo obtener información del usuario de Google");
        }

        String correo   = (String) info.get("email");
        String googleId = (String) info.get("sub");
        String nombres  = info.getOrDefault("given_name",  info.getOrDefault("name", "Usuario")).toString();
        String apellidos= info.getOrDefault("family_name", "").toString();

        // Buscar usuario existente o crear uno nuevo
        Usuario usuario = usuarioRepository.findByCorreo(correo).orElseGet(() -> {

            Rol rolCliente = rolRepository.findByNombreRol(Rol.NombreRol.CLIENTE)
                    .orElseThrow(() -> new ResourceNotFoundException("Rol CLIENTE no encontrado"));

            return usuarioRepository.save(Usuario.builder()
                    .nombres(nombres)
                    .apellidos(apellidos.isBlank() ? "-" : apellidos)
                    .correo(correo)
                    .contrasena(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .googleId(googleId)
                    .rol(rolCliente)
                    .estado(true)
                    .build());
        });

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

        // Si el admin tiene 2FA activo: no emitir JWT, pedir código TOTP
        boolean esAdmin = usuario.getRol().getNombreRol() == Rol.NombreRol.ADMINISTRADOR;
        if (esAdmin && Boolean.TRUE.equals(usuario.getTotpEnabled())) {
            return AuthResponseDto.builder()
                    .requiresTwoFactor(true)
                    .idUsuario(usuario.getIdUsuario())
                    .build();
        }

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
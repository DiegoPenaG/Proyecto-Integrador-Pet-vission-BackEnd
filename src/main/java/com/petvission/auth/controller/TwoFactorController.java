package com.petvission.auth.controller;

import com.petvission.auth.dto.AuthResponseDto;
import com.petvission.auth.dto.TwoFactorEnableDto;
import com.petvission.auth.dto.TwoFactorVerifyDto;
import com.petvission.auth.service.TotpService;
import com.petvission.security.service.JwtService;
import com.petvission.shared.exception.ResourceNotFoundException;
import com.petvission.shared.response.ApiResponse;
import com.petvission.usuario.model.Usuario;
import com.petvission.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/2fa")
@RequiredArgsConstructor
public class TwoFactorController {

    private final TotpService totpService;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    /*
     * SETUP — genera secret y devuelve el QR como data URL
     * El secret se guarda temporalmente; el admin debe confirmar con /enable.
     */
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/setup")
    public ResponseEntity<ApiResponse<Map<String, String>>> setup(Authentication auth) {
        Usuario usuario = (Usuario) auth.getPrincipal();
        String secret = totpService.generarSecret();

        // Guardar el secret pendiente de confirmación (lo habilitamos solo cuando el admin verifica)
        usuario.setTotpSecret(secret);
        usuarioRepository.save(usuario);

        String qrDataUrl = totpService.generarQrDataUrl(usuario.getCorreo(), secret);

        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "qrDataUrl", qrDataUrl,
                "secret", secret
        )));
    }

    /*
     * ENABLE — confirma el primer código y activa 2FA
     */
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/enable")
    public ResponseEntity<ApiResponse<String>> enable(
            @RequestBody TwoFactorEnableDto dto,
            Authentication auth
    ) {
        Usuario usuario = (Usuario) auth.getPrincipal();

        if (usuario.getTotpSecret() == null) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Primero genera el QR en /setup")
            );
        }

        if (!totpService.verificarCodigo(usuario.getTotpSecret(), dto.getCodigo())) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Código incorrecto")
            );
        }

        usuario.setTotpEnabled(true);
        usuarioRepository.save(usuario);

        return ResponseEntity.ok(ApiResponse.success("2FA activado correctamente"));
    }

    /*
     * VERIFY — verifica el código TOTP durante el login (sin JWT)
     * Retorna el JWT completo si el código es válido.
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<AuthResponseDto>> verify(
            @RequestBody TwoFactorVerifyDto dto
    ) {
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!Boolean.TRUE.equals(usuario.getTotpEnabled()) || usuario.getTotpSecret() == null) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("2FA no está activo para este usuario")
            );
        }

        if (!totpService.verificarCodigo(usuario.getTotpSecret(), dto.getCodigo())) {
            return ResponseEntity.status(401).body(
                    ApiResponse.error("Código incorrecto")
            );
        }

        String token = jwtService.generarToken(usuario);

        return ResponseEntity.ok(ApiResponse.success(
                AuthResponseDto.builder()
                        .token(token)
                        .tipo("Bearer")
                        .idUsuario(usuario.getIdUsuario())
                        .nombres(usuario.getNombres())
                        .apellidos(usuario.getApellidos())
                        .correo(usuario.getCorreo())
                        .rol(usuario.getRol().getNombreRol())
                        .build()
        ));
    }

    /*
     * DISABLE — desactiva 2FA
     */
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/disable")
    public ResponseEntity<ApiResponse<String>> disable(Authentication auth) {
        Usuario usuario = (Usuario) auth.getPrincipal();
        usuario.setTotpEnabled(false);
        usuario.setTotpSecret(null);
        usuarioRepository.save(usuario);
        return ResponseEntity.ok(ApiResponse.success("2FA desactivado"));
    }
}

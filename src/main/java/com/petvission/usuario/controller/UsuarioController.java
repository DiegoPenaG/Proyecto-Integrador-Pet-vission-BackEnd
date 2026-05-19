// src/main/java/com/petvission/usuario/controller/UsuarioController.java

package com.petvission.usuario.controller;

import com.petvission.shared.response.ApiResponse;
import com.petvission.usuario.dto.UsuarioRequestDto;
import com.petvission.usuario.dto.UsuarioResponseDto;
import com.petvission.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // GET /api/usuarios
    // Solo ADMINISTRADOR puede listar todos los usuarios
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<List<UsuarioResponseDto>>> listar() {
        return ResponseEntity.ok(
                ApiResponse.success(usuarioService.listarActivos())
        );
    }

    // GET /api/usuarios/{id}
    // Solo ADMINISTRADOR puede ver un usuario por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<UsuarioResponseDto>> obtener(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(usuarioService.obtenerPorId(id))
        );
    }

    // GET /api/usuarios/veterinarios
    // Cualquier usuario autenticado puede ver los veterinarios
    // Se usa para elegir veterinario al agendar una cita
    @GetMapping("/veterinarios")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<UsuarioResponseDto>>> listarVeterinarios() {
        return ResponseEntity.ok(
                ApiResponse.success(usuarioService.listarVeterinarios())
        );
    }

    // PUT /api/usuarios/{id}
    // El usuario puede actualizar sus propios datos básicos
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<UsuarioResponseDto>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDto dto) {
        return ResponseEntity.ok(
                ApiResponse.success(usuarioService.actualizar(id, dto))
        );
    }

    // DELETE /api/usuarios/{id}
    // Soft delete — solo desactiva el usuario
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> desactivar(
            @PathVariable Long id) {
        usuarioService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
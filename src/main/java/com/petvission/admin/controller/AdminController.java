package com.petvission.admin.controller;

import com.petvission.shared.response.ApiResponse;
import com.petvission.usuario.dto.CreateVeterinarioDto;
import com.petvission.usuario.dto.UsuarioResponseDto;
import com.petvission.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UsuarioService usuarioService;

    @PostMapping("/veterinarios")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<UsuarioResponseDto>> crearVeterinario(
            @Valid @RequestBody CreateVeterinarioDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(usuarioService.crearVeterinario(dto))
        );
    }
}

package com.petvission.mascota.controller;

import com.petvission.mascota.dto.MascotaRequestDto;
import com.petvission.mascota.dto.MascotaResponseDto;
import com.petvission.mascota.dto.ReasignarMascotaDto;
import com.petvission.mascota.service.MascotaService;
import com.petvission.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mascotas")
@RequiredArgsConstructor
public class MascotaController {

    private final MascotaService mascotaService;

    // GET /api/mascotas/todas
    @GetMapping("/todas")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<List<MascotaResponseDto>>> listarTodas() {
        return ResponseEntity.ok(
                ApiResponse.success(mascotaService.listarTodas())
        );
    }

    // GET /api/mascotas/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('VETERINARIO') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<MascotaResponseDto>> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(mascotaService.obtenerPorId(id))
        );
    }

    // GET /api/mascotas/usuario/{idUsuario}
    @GetMapping("/usuario/{idUsuario}")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('VETERINARIO') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<List<MascotaResponseDto>>> listar(
            @PathVariable Long idUsuario) {
        return ResponseEntity.ok(
                ApiResponse.success(mascotaService.listarPorUsuario(idUsuario))
        );
    }

    // POST /api/mascotas/usuario/{idUsuario}
    @PostMapping("/usuario/{idUsuario}")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<MascotaResponseDto>> crear(
            @PathVariable Long idUsuario,
            @Valid @RequestBody MascotaRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(mascotaService.crear(idUsuario, dto))
        );
    }

    // PUT /api/mascotas/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<MascotaResponseDto>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody MascotaRequestDto dto) {
        return ResponseEntity.ok(
                ApiResponse.success(mascotaService.actualizar(id, dto))
        );
    }

    // DELETE /api/mascotas/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> eliminar(
            @PathVariable Long id) {
        mascotaService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // PATCH /api/mascotas/{id}/reasignar
    @PatchMapping("/{id}/reasignar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<MascotaResponseDto>> reasignar(
            @PathVariable Long id,
            @Valid @RequestBody ReasignarMascotaDto dto) {
        return ResponseEntity.ok(
                ApiResponse.success(mascotaService.reasignarDueno(id, dto.getIdNuevoUsuario()))
        );
    }
}
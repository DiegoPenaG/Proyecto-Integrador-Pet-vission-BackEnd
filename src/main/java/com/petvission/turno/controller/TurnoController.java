package com.petvission.turno.controller;

import com.petvission.shared.response.ApiResponse;
import com.petvission.turno.dto.TurnoDetalleResponseDto;
import com.petvission.turno.dto.TurnoRequestDto;
import com.petvission.turno.dto.TurnoResponseDto;
import com.petvission.turno.service.TurnoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/turnos")
@RequiredArgsConstructor
public class TurnoController {

    private final TurnoService turnoService;

    /*
     * LISTAR TODOS LOS TURNOS — ADMIN
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<List<TurnoResponseDto>>> listarTodos() {
        return ResponseEntity.ok(
                ApiResponse.success(turnoService.listarTodos())
        );
    }

    /*
     * TURNOS POR VETERINARIO — ADMIN, VETERINARIO
     */
    @GetMapping("/veterinario/{idVeterinario}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('VETERINARIO')")
    public ResponseEntity<ApiResponse<List<TurnoResponseDto>>> listarPorVeterinario(
            @PathVariable Long idVeterinario
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(turnoService.listarPorVeterinario(idVeterinario))
        );
    }

    /*
     * DETALLES DISPONIBLES DE UN TURNO — TODOS
     */
    @GetMapping("/{id}/detalles/disponibles")
    public ResponseEntity<ApiResponse<List<TurnoDetalleResponseDto>>> listarDetallesDisponibles(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(turnoService.listarDetallesDisponibles(id))
        );
    }

    /*
     * CREAR TURNO CON DETALLES — SOLO ADMIN
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<TurnoResponseDto>> crear(
            @Valid @RequestBody TurnoRequestDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(turnoService.crear(dto))
        );
    }

    /*
     * ACTIVAR TURNO — SOLO ADMIN
     */
    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<TurnoResponseDto>> activar(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(turnoService.activar(id))
        );
    }

    /*
     * DESACTIVAR TURNO — SOLO ADMIN
     */
    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<TurnoResponseDto>> desactivar(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(turnoService.desactivar(id))
        );
    }
}

package com.petvission.historialClinico.controller;

import com.petvission.historialClinico.dto.HistorialClinicoRequestDto;
import com.petvission.historialClinico.dto.HistorialClinicoResponseDto;
import com.petvission.historialClinico.dto.NuevaConsultaRequestDto;
import com.petvission.historialClinico.service.HistorialClinicoService;

import com.petvission.shared.response.ApiResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/historial")
@RequiredArgsConstructor
public class HistorialClinicoController {

    private final HistorialClinicoService historialService;

    /*
     * REGISTRAR DIAGNÓSTICO
     */
    @PreAuthorize("hasRole('VETERINARIO')")
    @PatchMapping("/{idHistorial}/diagnostico")
    public ResponseEntity<ApiResponse<HistorialClinicoResponseDto>>
    registrarDiagnostico(
            @PathVariable Long idHistorial,
            @RequestBody String diagnostico
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        historialService.registrarDiagnostico(
                                idHistorial,
                                diagnostico
                        )
                )
        );
    }

    /*
     * REGISTRAR TRATAMIENTO
     */
    @PreAuthorize("hasRole('VETERINARIO')")
    @PatchMapping("/{idHistorial}/tratamiento")
    public ResponseEntity<ApiResponse<HistorialClinicoResponseDto>>
    registrarTratamiento(
            @PathVariable Long idHistorial,
            @RequestParam String tratamiento,
            @RequestParam String receta
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        historialService.registrarTratamiento(
                                idHistorial,
                                tratamiento,
                                receta
                        )
                )
        );
    }

    /*
     * REGISTRAR OBSERVACIÓN
     */
    @PreAuthorize("hasRole('VETERINARIO')")
    @PostMapping
    public ResponseEntity<ApiResponse<HistorialClinicoResponseDto>>
    registrarObservacion(
            @RequestBody HistorialClinicoRequestDto dto
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        historialService.registrarObservacion(dto)
                )
        );
    }

    /*
     * OBTENER HISTORIAL MASCOTA
     */
    @PreAuthorize("hasAnyRole('VETERINARIO','ADMINISTRADOR','CLIENTE')")
    @GetMapping("/mascota/{idMascota}")
    public ResponseEntity<ApiResponse<List<HistorialClinicoResponseDto>>>
    obtenerHistorialMascota(
            @PathVariable Long idMascota
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        historialService.obtenerHistorialMascota(
                                idMascota
                        )
                )
        );
    }

    /*
     * CREAR CONSULTA COMPLETA (vet extraído del JWT)
     */
    @PreAuthorize("hasRole('VETERINARIO')")
    @PostMapping("/mascota/{idMascota}")
    public ResponseEntity<ApiResponse<HistorialClinicoResponseDto>>
    crearConsulta(
            @PathVariable Long idMascota,
            @Valid @RequestBody NuevaConsultaRequestDto dto
    ) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                        historialService.crearConsulta(idMascota, dto)
                )
        );
    }

    /*
     * OBTENER HISTORIAL POR RESERVA (modo edición en ficha)
     */
    @PreAuthorize("hasAnyRole('VETERINARIO','ADMINISTRADOR')")
    @GetMapping("/reserva/{idReserva}")
    public ResponseEntity<ApiResponse<HistorialClinicoResponseDto>>
    obtenerHistorialPorReserva(@PathVariable Long idReserva) {
        Optional<HistorialClinicoResponseDto> result =
                historialService.obtenerHistorialPorReserva(idReserva);
        return ResponseEntity.ok(ApiResponse.success(result.orElse(null)));
    }

    /*
     * EDITAR CONSULTA COMPLETA (solo el vet que la creó)
     */
    @PreAuthorize("hasRole('VETERINARIO')")
    @PutMapping("/{idHistorial}")
    public ResponseEntity<ApiResponse<HistorialClinicoResponseDto>>
    editarConsulta(
            @PathVariable Long idHistorial,
            @Valid @RequestBody NuevaConsultaRequestDto dto
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(historialService.editarConsulta(idHistorial, dto))
        );
    }
}
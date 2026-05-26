package com.petvission.atencion.controller;

import com.petvission.atencion.dto.HistorialClinicoRequestDto;
import com.petvission.atencion.service.HistorialClinicoService;

import com.petvission.shared.response.ApiResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/historial")
@RequiredArgsConstructor
public class HistorialClinicoController {

    private final HistorialClinicoService historialService;

    /*
     * REGISTRAR DIAGNÓSTICO
     */
    @PatchMapping("/{idHistorial}/diagnostico")
    public ResponseEntity<ApiResponse<?>>
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
    @PatchMapping("/{idHistorial}/tratamiento")
    public ResponseEntity<ApiResponse<?>>
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
    @PostMapping
    public ResponseEntity<ApiResponse<?>>
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
    @GetMapping("/mascota/{idMascota}")
    public ResponseEntity<ApiResponse<?>>
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
}
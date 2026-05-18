package org.example.petvission.mascota.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.petvission.Common.Response.ApiResponse;
import org.example.petvission.mascota.dto.MascotaResponseDto;
import org.example.petvission.mascota.service.MascotaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * CONTROLADOR DE MASCOTAS
 * MANEJA LOS ENDPOINTS RELACIONADOS
 * CON EL REGISTRO Y ADMINISTRACIÓN
 * DE MASCOTAS EN EL SISTEMA.
 */

@RestController
@RequestMapping("/api/mascotas")
@RequiredArgsConstructor
public class MascotaController {

    /*
     * INYECCIÓN DEL SERVICIO DE MASCOTAS
     */
    private final MascotaService mascotaService;

    /*
     * MÉTODO PARA LISTAR LAS MASCOTAS
     * ASOCIADAS A UN USUARIO
     */
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<ApiResponse<List<MascotaResponseDto>>> listar(
            @PathVariable Long idUsuario) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        mascotaService.listarPorUsuario(idUsuario)
                )
        );
    }

    /*
     * MÉTODO PARA REGISTRAR
     * UNA NUEVA MASCOTA
     */
    @PostMapping("/usuario/{idUsuario}")
    public ResponseEntity<ApiResponse<MascotaResponseDto>> crear(
            @PathVariable Long idUsuario,
            @Valid @RequestBody MascotaRequestDto dto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                        mascotaService.crear(idUsuario, dto)
                )
        );
    }

    /*
     * MÉTODO PARA ACTUALIZAR
     * LOS DATOS DE UNA MASCOTA
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MascotaResponseDto>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody MascotaRequestDto dto) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        mascotaService.actualizar(id, dto)
                )
        );
    }

    /*
     * MÉTODO PARA ELIMINAR
     * UNA MASCOTA MEDIANTE SU ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(
            @PathVariable Long id) {

        mascotaService.eliminar(id);

        return ResponseEntity.ok(
                ApiResponse.success(null)
        );
    }
}
package com.petvission.servicio.controller;

import com.petvission.servicio.dto.ServicioRequestDto;
import com.petvission.servicio.dto.ServicioResponseDto;
import com.petvission.servicio.model.TipoServicio;
import com.petvission.servicio.service.ServicioService;
import com.petvission.shared.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
@RequiredArgsConstructor
public class ServicioController {

    private final ServicioService servicioService;

    /*
     * CREAR SERVICIO
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ServicioResponseDto>> crearServicio(
            @Valid @RequestBody ServicioRequestDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(servicioService.crearServicio(dto))
        );
    }

    /*
     * OBTENER TODOS LOS SERVICIOS
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ServicioResponseDto>>> obtenerTodos() {
        return ResponseEntity.ok(ApiResponse.success(servicioService.obtenerTodos()));
    }

    /*
     * OBTENER SERVICIOS ACTIVOS
     * GET /api/servicios/activos
     * GET /api/servicios/activos?tipo=VACUNACION
     * GET /api/servicios/activos?tipo=LABORATORIO
     */
    @GetMapping("/activos")
    public ResponseEntity<ApiResponse<List<ServicioResponseDto>>> obtenerActivos(
            @RequestParam(required = false) TipoServicio tipo
    ) {
        return ResponseEntity.ok(ApiResponse.success(servicioService.obtenerActivos(tipo)));
    }

    /*
     * OBTENER SERVICIO POR ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServicioResponseDto>> obtenerPorId(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(ApiResponse.success(servicioService.obtenerPorId(id)));
    }

    /*
     * ACTUALIZAR SERVICIO
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ServicioResponseDto>> actualizarServicio(
            @PathVariable Integer id,
            @Valid @RequestBody ServicioRequestDto dto
    ) {
        return ResponseEntity.ok(ApiResponse.success(servicioService.actualizarServicio(id, dto)));
    }

    /*
     * DESACTIVAR SERVICIO
     */
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<ApiResponse<ServicioResponseDto>> desactivarServicio(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(ApiResponse.success(servicioService.desactivarServicio(id)));
    }
}

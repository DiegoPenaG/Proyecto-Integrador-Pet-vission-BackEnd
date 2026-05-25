package com.petvission.servicio.controller;

import com.petvission.servicio.dto.ServicioRequestDto;
import com.petvission.servicio.dto.ServicioResponseDto;
import com.petvission.servicio.service.ServicioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<ServicioResponseDto> crearServicio(
            @Valid @RequestBody ServicioRequestDto dto
    ) {
        return ResponseEntity.ok(
                servicioService.crearServicio(dto)
        );
    }

    /*
     * OBTENER TODOS LOS SERVICIOS
     */
    @GetMapping
    public ResponseEntity<List<ServicioResponseDto>> obtenerTodos() {
        return ResponseEntity.ok(
                servicioService.obtenerTodos()
        );
    }

    /*
     * OBTENER SERVICIOS ACTIVOS
     */
    @GetMapping("/activos")
    public ResponseEntity<List<ServicioResponseDto>> obtenerActivos() {
        return ResponseEntity.ok(
                servicioService.obtenerActivos()
        );
    }

    /*
     * OBTENER SERVICIOS POR CATEGORÍA
     */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ServicioResponseDto>> obtenerPorCategoria(
            @PathVariable String categoria
    ) {
        return ResponseEntity.ok(
                servicioService.obtenerPorCategoria(categoria)
        );
    }

    /*
     * OBTENER SERVICIO POR ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ServicioResponseDto> obtenerPorId(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(
                servicioService.obtenerPorId(id)
        );
    }

    /*
     * ACTUALIZAR SERVICIO
     */
    @PutMapping("/{id}")
    public ResponseEntity<ServicioResponseDto> actualizarServicio(
            @PathVariable Integer id,
            @Valid @RequestBody ServicioRequestDto dto
    ) {
        return ResponseEntity.ok(
                servicioService.actualizarServicio(id, dto)
        );
    }

    /*
     * DESACTIVAR SERVICIO (baja lógica)
     */
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<ServicioResponseDto> desactivarServicio(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(
                servicioService.desactivarServicio(id)
        );
    }
}

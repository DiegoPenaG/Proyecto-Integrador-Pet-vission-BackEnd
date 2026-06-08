package com.petvission.notificacion.controller;

import com.petvission.notificacion.service.CitaConfirmacionService;
import com.petvission.reserva.model.EstadoReserva;
import com.petvission.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class CitaConfirmacionController {

    private final CitaConfirmacionService citaConfirmacionService;

    /**
     * Confirmación desde el enlace del email de recordatorio.
     * Público — token en query param reemplaza la autenticación JWT.
     */
    @PostMapping("/{id}/confirmar")
    public ResponseEntity<ApiResponse<String>> confirmarCita(
            @PathVariable Long id,
            @RequestParam String token) {

        citaConfirmacionService.confirmarCita(id, token);
        return ResponseEntity.ok(ApiResponse.success("¡Cita confirmada! Nos vemos pronto."));
    }

    /**
     * Consulta el estado actual de una reserva.
     * Requiere autenticación JWT.
     */
    @GetMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<EstadoReserva>> obtenerEstadoCita(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(citaConfirmacionService.obtenerEstadoCita(id)));
    }
}

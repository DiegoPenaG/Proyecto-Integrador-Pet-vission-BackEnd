package com.petvission.reserva.controller;

import com.petvission.reserva.dto.*;
import com.petvission.reserva.service.ReservaService;
import com.petvission.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @GetMapping("/agenda")
    public ResponseEntity<ApiResponse<List<AgendaVeterinarioDto>>> obtenerAgendaVeterinarios() {
        return ResponseEntity.ok(ApiResponse.success(reservaService.obtenerAgendaVeterinarios()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<List<ReservaUsuarioDto>>> obtenerTodasLasReservas() {
        return ResponseEntity.ok(ApiResponse.success(reservaService.obtenerTodasLasReservas()));
    }

    @GetMapping("/agenda/veterinario/{idVeterinario}")
    public ResponseEntity<ApiResponse<List<ReservaUsuarioDto>>> obtenerAgendaVeterinario(
            @PathVariable Long idVeterinario) {
        return ResponseEntity.ok(ApiResponse.success(
                reservaService.obtenerAgendaMensualVeterinario(idVeterinario)));
    }

    @GetMapping("/disponibilidad")
    public ResponseEntity<?> obtenerDisponibilidadBasica() {
        return ResponseEntity.ok(reservaService.obtenerDisponibilidadBasica());
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<ApiResponse<List<ReservaUsuarioDto>>> obtenerReservasUsuario(
            @PathVariable Long idUsuario) {
        return ResponseEntity.ok(ApiResponse.success(
                reservaService.obtenerReservasPorUsuario(idUsuario)));
    }

    @GetMapping("/veterinario/{idVeterinario}")
    public ResponseEntity<ApiResponse<List<ReservaUsuarioDto>>> obtenerReservasVeterinario(
            @PathVariable Long idVeterinario) {
        return ResponseEntity.ok(ApiResponse.success(
                reservaService.obtenerReservasVeterinario(idVeterinario)));
    }

    @GetMapping("/veterinario/{idVeterinario}/hoy")
    public ResponseEntity<ApiResponse<List<ReservaUsuarioDto>>> obtenerReservasVeterinarioHoy(
            @PathVariable Long idVeterinario) {
        return ResponseEntity.ok(ApiResponse.success(
                reservaService.obtenerReservasVeterinarioHoy(idVeterinario)));
    }

    @GetMapping("/veterinario/{idVeterinario}/pacientes")
    public ResponseEntity<ApiResponse<List<PacienteVetDto>>> obtenerPacientesVeterinario(
            @PathVariable Long idVeterinario) {
        return ResponseEntity.ok(ApiResponse.success(
                reservaService.obtenerPacientesVeterinario(idVeterinario)));
    }

    @GetMapping("/fecha")
    public ResponseEntity<ApiResponse<List<ReservaUsuarioDto>>> obtenerReservasPorFecha(
            @RequestParam String fecha) {
        return ResponseEntity.ok(ApiResponse.success(
                reservaService.obtenerReservasPorFecha(LocalDate.parse(fecha))));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReservaResponseDto>> agendarReserva(
            @Valid @RequestBody ReservaRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(reservaService.agendarReservaDto(dto)));
    }

    // El cliente o vet asignado o admin pueden cancelar (lógica en service)
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ApiResponse<ReservaUsuarioDto>> cancelarReserva(
            @PathVariable Long id,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(reservaService.cancelarReserva(id, auth)));
    }

    // El vet o admin inicia la atención: PENDIENTE/CONFIRMADA → EN_ATENCION
    @PatchMapping("/{id}/iniciarAtencion")
    @PreAuthorize("hasAnyRole('VETERINARIO','ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<ReservaUsuarioDto>> iniciarAtencion(
            @PathVariable Long id,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(reservaService.iniciarAtencion(id, auth)));
    }

    // Cliente o veterinario confirman una reserva PENDIENTE → CONFIRMADA
    @PatchMapping("/{id}/confirmar")
    @PreAuthorize("hasAnyRole('VETERINARIO','CLIENTE')")
    public ResponseEntity<ApiResponse<ReservaUsuarioDto>> confirmarReserva(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(reservaService.confirmarReserva(id)));
    }

    // Solo el veterinario puede completar (CONFIRMADA → COMPLETADA)
    @PatchMapping("/{id}/completar")
    @PreAuthorize("hasRole('VETERINARIO')")
    public ResponseEntity<ApiResponse<ReservaUsuarioDto>> completarReserva(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(reservaService.completarReserva(id)));
    }

    // El cliente dueño, vet asignado o admin pueden reprogramar (lógica en service)
    @PatchMapping("/{id}/reprogramar")
    public ResponseEntity<ApiResponse<ReservaUsuarioDto>> reprogramarReserva(
            @PathVariable Long id,
            @RequestBody ReprogramarReservaDto dto,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(
                reservaService.reprogramarReserva(id, dto, auth)));
    }
}

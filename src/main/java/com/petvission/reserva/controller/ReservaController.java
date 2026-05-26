package com.petvission.reserva.controller;

import com.petvission.reserva.dto.AgendaVeterinarioDto;
import com.petvission.reserva.dto.ReservaRequestDto;
import com.petvission.reserva.dto.ReservaResponseDto;
import com.petvission.reserva.dto.ReservaUsuarioDto;
import com.petvission.reserva.dto.ReprogramarReservaDto;

import com.petvission.reserva.service.ReservaService;

import com.petvission.shared.response.ApiResponse;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    /*
     * AGENDA GENERAL
     */
    @GetMapping("/agenda")
    public ResponseEntity<ApiResponse<List<AgendaVeterinarioDto>>>
    obtenerAgendaVeterinarios() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        reservaService.obtenerAgendaVeterinarios()
                )
        );
    }

    /*
     * TODAS LAS RESERVAS
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<List<ReservaUsuarioDto>>>
    obtenerTodasLasReservas() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        reservaService.obtenerTodasLasReservas()
                )
        );
    }

    /*
     * AGENDA VETERINARIO
     */
    @GetMapping("/agenda/veterinario/{idVeterinario}")
    public ResponseEntity<ApiResponse<List<ReservaUsuarioDto>>>
    obtenerAgendaVeterinario(
            @PathVariable Long idVeterinario
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        reservaService.obtenerAgendaMensualVeterinario(
                                idVeterinario
                        )
                )
        );
    }

    /*
     * DISPONIBILIDAD
     */
    @GetMapping("/disponibilidad")
    public ResponseEntity<?> obtenerDisponibilidadBasica() {

        return ResponseEntity.ok(
                reservaService.obtenerDisponibilidadBasica()
        );
    }

    /*
     * RESERVAS USUARIO
     */
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<?> obtenerReservasUsuario(
            @PathVariable Long idUsuario
    ) {

        return ResponseEntity.ok(
                reservaService.obtenerReservasPorUsuario(
                        idUsuario
                )
        );
    }

    /*
     * RESERVAS VETERINARIO
     */
    @GetMapping("/veterinario/{idVeterinario}")
    public ResponseEntity<ApiResponse<List<ReservaUsuarioDto>>>
    obtenerReservasVeterinario(
            @PathVariable Long idVeterinario
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        reservaService.obtenerReservasVeterinario(
                                idVeterinario
                        )
                )
        );
    }

    /*
     * RESERVAS POR FECHA
     */
    @GetMapping("/fecha")
    public ResponseEntity<ApiResponse<List<ReservaUsuarioDto>>>
    obtenerReservasPorFecha(
            @RequestParam String fecha
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        reservaService.obtenerReservasPorFecha(
                                LocalDate.parse(fecha)
                        )
                )
        );
    }

    /*
     * CANCELAR
     */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ApiResponse<ReservaUsuarioDto>>
    cancelarReserva(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        reservaService.cancelarReserva(id)
                )
        );
    }

    /*
     * REPROGRAMAR
     */
    @PatchMapping("/{id}/reprogramar")
    public ResponseEntity<ApiResponse<ReservaUsuarioDto>>
    reprogramarReserva(
            @PathVariable Long id,
            @RequestBody ReprogramarReservaDto dto
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        reservaService.reprogramarReserva(id, dto)
                )
        );
    }

    /*
     * AGENDAR
     */
    @PostMapping
    public ResponseEntity<ReservaResponseDto>
    agendarReserva(
            @Valid @RequestBody ReservaRequestDto dto
    ) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        reservaService.agendarReservaDto(dto)
                );
    }
}
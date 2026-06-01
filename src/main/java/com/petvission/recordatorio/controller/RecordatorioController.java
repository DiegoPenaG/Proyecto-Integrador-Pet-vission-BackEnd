package com.petvission.recordatorio.controller;

import com.petvission.recordatorio.dto.RecordatorioResponseDto;
import com.petvission.recordatorio.service.RecordatorioService;
import com.petvission.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recordatorios")
@RequiredArgsConstructor
public class RecordatorioController {

    private final RecordatorioService recordatorioService;

    // El cliente confirma su recordatorio → dispara PENDIENTE → CONFIRMADA en la reserva
    @PatchMapping("/{id}/confirmar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ApiResponse<RecordatorioResponseDto>> confirmarRecordatorio(
            @PathVariable Long id,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(
                recordatorioService.confirmarRecordatorio(id, auth)));
    }
}

package com.petvission.dev;

import com.petvission.notificacion.service.EmailService;
import com.petvission.recordatorio.model.Recordatorio;
import com.petvission.recordatorio.repository.RecordatorioRepository;
import com.petvission.reserva.model.Reserva;
import com.petvission.reserva.repository.ReservaRepository;
import java.time.LocalDateTime;
import com.petvission.shared.exception.ResourceNotFoundException;
import com.petvission.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/dev")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class DevToolsController {

    private final ReservaRepository reservaRepository;
    private final RecordatorioRepository recordatorioRepository;
    private final EmailService emailService;

    /**
     * Dispara un email de recordatorio de prueba a la reserva indicada.
     * Solo admin. Remover antes de pasar a producción real.
     */
    @PostMapping("/test-email/{idReserva}")
    public ResponseEntity<ApiResponse<String>> testEmail(@PathVariable Long idReserva) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada: " + idReserva));

        String token = UUID.randomUUID().toString();

        // Upsert del recordatorio: sin esto el link de confirmación falla (token no existe en BD)
        Recordatorio rec = recordatorioRepository
                .findByReserva_IdReserva(idReserva)
                .orElse(Recordatorio.builder().reserva(reserva).build());
        rec.setConfirmationToken(token);
        rec.setEnviado(true);
        rec.setConfirmado(false);
        rec.setFechaEnvio(LocalDateTime.now());
        rec.setFechaConfirmacion(null);
        recordatorioRepository.save(rec);

        emailService.enviarRecordatorio7Dias(reserva, token);

        String destinatario = reserva.getUsuario().getCorreo();
        String linkGenerado  = "/confirmar-cita/" + idReserva + "?token=" + token;

        return ResponseEntity.ok(ApiResponse.success(
                "Email enviado a [" + destinatario + "]. Link: " + linkGenerado
        ));
    }
}

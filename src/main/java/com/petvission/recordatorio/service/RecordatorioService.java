package com.petvission.recordatorio.service;

import com.petvission.recordatorio.dto.RecordatorioResponseDto;
import com.petvission.recordatorio.model.Recordatorio;
import com.petvission.recordatorio.repository.RecordatorioRepository;
import com.petvission.reserva.service.ReservaService;
import com.petvission.shared.exception.ResourceNotFoundException;
import com.petvission.shared.exception.UnauthorizedException;
import com.petvission.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RecordatorioService {

    private final RecordatorioRepository recordatorioRepository;
    private final ReservaService reservaService;

    @Transactional
    public RecordatorioResponseDto confirmarRecordatorio(Long id, Authentication auth) {

        Recordatorio recordatorio = recordatorioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recordatorio no encontrado"));

        // Solo el cliente dueño de la reserva puede confirmar
        Usuario usuario = (Usuario) auth.getPrincipal();
        Long idDueno = recordatorio.getReserva().getUsuario().getIdUsuario();
        if (!idDueno.equals(usuario.getIdUsuario())) {
            throw new UnauthorizedException(
                    "No tiene permiso para confirmar este recordatorio");
        }

        if (recordatorio.getConfirmado()) {
            throw new IllegalStateException("El recordatorio ya fue confirmado");
        }

        recordatorio.setConfirmado(true);
        recordatorio.setFechaConfirmacion(LocalDateTime.now());
        recordatorioRepository.save(recordatorio);

        // Dispara PENDIENTE → CONFIRMADA en la reserva asociada
        reservaService.confirmarReserva(recordatorio.getReserva().getIdReserva());

        return toDto(recordatorio);
    }

    private RecordatorioResponseDto toDto(Recordatorio r) {
        return RecordatorioResponseDto.builder()
                .id(r.getId())
                .idReserva(r.getReserva().getIdReserva())
                .enviado(r.getEnviado())
                .confirmado(r.getConfirmado())
                .fechaEnvio(r.getFechaEnvio())
                .fechaConfirmacion(r.getFechaConfirmacion())
                .build();
    }
}

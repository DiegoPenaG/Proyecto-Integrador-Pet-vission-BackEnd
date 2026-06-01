package com.petvission.recordatorio.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordatorioResponseDto {

    private Long id;
    private Long idReserva;
    private Boolean enviado;
    private Boolean confirmado;
    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaConfirmacion;
}

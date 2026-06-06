package com.petvission.vacunacion.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacunacionDesdeConsultaDto {

    private Long idMascota;
    private Long idVacuna;
    private Long idHistorial;
    private LocalDate fechaAplicacion;
    private LocalDate fechaProxima;
    private String lote;
}

package com.petvission.vacunacion.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacunacionRequestDto {

    private Long idMascota;

    private Long idVacuna;

    private Long idHistorialClinico;   // ← renombrado desde idAtencion

    private Long idVeterinario;

    private LocalDate fechaAplicacion;

    private LocalDate proximaDosis;

    private String lote;               // ← campo del modelo ER que faltaba
}
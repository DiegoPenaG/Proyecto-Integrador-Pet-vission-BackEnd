package com.petvission.vacunacion.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacunacionResponseDto {

    private Long idVacunacion;

    private String nombreMascota;

    private String nombreVacuna;        // nombre desde VacunaCatalogo

    private String nombreVeterinario;

    private LocalDate fechaAplicacion;

    private LocalDate proximaDosis;

    private String lote;
}
package com.petvission.vacunacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacunacionRequestDto {

    private Long idMascota;

    private Long idVacuna;

    private Long idAtencion;

    private LocalDate fechaAplicacion;

    private LocalDate proximaDosis;
}
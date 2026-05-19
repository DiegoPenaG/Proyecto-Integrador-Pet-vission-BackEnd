package com.petvission.cita.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ReprogramarCitaDto {

    private LocalDate nuevaFecha;
    private LocalTime nuevaHora;
}

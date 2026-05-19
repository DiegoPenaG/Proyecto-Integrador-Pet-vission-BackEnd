package org.example.petvission.horario.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class HorarioDto {

    private Long id;

    private LocalDate fecha;

    private LocalTime hora;

    private String nombreVeterinario;

    private Boolean disponible;
}

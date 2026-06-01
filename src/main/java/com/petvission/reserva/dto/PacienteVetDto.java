package com.petvission.reserva.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PacienteVetDto {
    private Long idMascota;
    private String nombreMascota;
    private String especie;
    private String raza;
    private String nombreDueno;
    private LocalDate ultimaVisita;
    private Boolean activo;
    private Boolean animalGuia;
}

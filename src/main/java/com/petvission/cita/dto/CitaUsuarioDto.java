package com.petvission.cita.dto;

import lombok.Builder;
import lombok.Data;
import org.example.petvission.cita.model.EstadoCita;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class CitaUsuarioDto {

    private Long idCita;

    private String nombreCliente;

    private String nombreVeterinario;

    private LocalDate fecha;

    private LocalTime hora;

    private EstadoCita estado;

    private String motivo;
}
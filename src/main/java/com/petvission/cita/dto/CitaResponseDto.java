package com.petvission.cita.dto;

import com.petvission.cita.model.EstadoCita;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CitaResponseDto {

    private Long idCita;
    private Long idUsuario;
    private String nombreCliente;
    private Long idVeterinario;
    private String nombreVeterinario;
    private String especialidadVeterinario;
    private LocalDate fecha;
    private LocalTime hora;
    private String motivo;
    private EstadoCita estado;
}
package com.petvission.reserva.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.petvission.reserva.model.EstadoReserva;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaUsuarioDto {

    private Long idReserva;

    private String nombreCliente;

    private String nombreVeterinario;

    private LocalDate fecha;

    private LocalTime hora;

    private EstadoReserva estado;

    private String motivo;
}
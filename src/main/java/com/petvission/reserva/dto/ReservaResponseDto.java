package com.petvission.reserva.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.petvission.reserva.model.EstadoReserva;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaResponseDto {

    private Long idReserva;

    private Long idUsuario;

    private Long idVeterinario;

    private Long idTurnoDetalle;

    private LocalDate fecha;

    private LocalTime hora;

    private String motivo;

    private EstadoReserva estado;
}
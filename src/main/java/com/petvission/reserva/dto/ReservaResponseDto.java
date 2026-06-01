package com.petvission.reserva.dto;

import com.petvission.reserva.model.CategoriaReserva;
import com.petvission.reserva.model.EstadoReserva;
import lombok.*;

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
    private CategoriaReserva categoriaReserva;
    private LocalDate fecha;
    private LocalTime hora;
    private String motivo;
    private EstadoReserva estado;
}

package com.petvission.reserva.mapper;

import org.springframework.stereotype.Component;

import com.petvission.reserva.dto.ReservaResponseDto;
import com.petvission.reserva.model.Reserva;

@Component
public class ReservaMapper {

    public ReservaResponseDto toDto(
            Reserva reserva
    ) {

        return ReservaResponseDto.builder()
                .idReserva(
                        reserva.getIdReserva()
                )
                .idUsuario(
                        reserva.getUsuario().getIdUsuario()
                )
                .idVeterinario(
                        reserva.getVeterinario().getIdUsuario()
                )
                .fecha(
                        reserva.getFecha()
                )
                .hora(
                        reserva.getHora()
                )
                .motivo(
                        reserva.getMotivo()
                )
                .estado(
                        reserva.getEstado()
                )
                .build();
    }
}
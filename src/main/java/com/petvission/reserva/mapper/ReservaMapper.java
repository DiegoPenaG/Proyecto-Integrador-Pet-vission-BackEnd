package com.petvission.reserva.mapper;

import com.petvission.reserva.dto.ReservaUsuarioDto;
import org.springframework.stereotype.Component;

import com.petvission.reserva.dto.ReservaResponseDto;
import com.petvission.reserva.model.Reserva;

@Component
public class ReservaMapper {

    /*
     * RESERVA → RESPONSE DTO (para agendar)
     */
    public ReservaResponseDto toDto(Reserva reserva) {

        return ReservaResponseDto.builder()
                .idReserva(reserva.getIdReserva())
                .idUsuario(reserva.getUsuario().getIdUsuario())
                .idVeterinario(reserva.getVeterinario().getIdUsuario())
                .idTurnoDetalle(
                        reserva.getTurnoDetalle() != null
                                ? reserva.getTurnoDetalle().getId()
                                : null
                )
                .fecha(reserva.getFecha())
                .hora(reserva.getHora())
                .motivo(reserva.getMotivo())
                .estado(reserva.getEstado())
                .build();
    }
    /*
     * RESERVA → USUARIO DTO (para listados)
     */
    public ReservaUsuarioDto toUsuarioDto(Reserva reserva) {

        return ReservaUsuarioDto.builder()
                .idReserva(reserva.getIdReserva())
                .nombreCliente(
                        reserva.getUsuario().getNombres()
                                + " " +
                                reserva.getUsuario().getApellidos()
                )
                .nombreVeterinario(
                        reserva.getVeterinario().getUsuario().getNombres()
                                + " " +
                                reserva.getVeterinario().getUsuario().getApellidos()
                )
                .nombreMascota(
                        reserva.getMascota() != null
                                ? reserva.getMascota().getNombre()
                                : null
                )
                .fecha(reserva.getFecha())
                .hora(reserva.getHora())
                .estado(reserva.getEstado())
                .motivo(reserva.getMotivo())
                .build();
    }
}
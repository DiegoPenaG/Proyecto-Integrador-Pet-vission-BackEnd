package com.petvission.reserva.service;

import lombok.RequiredArgsConstructor;

import com.petvission.horario.repository.HorarioRepository;

import com.petvission.reserva.dto.AgendaVeterinarioDto;
import com.petvission.reserva.dto.ReservaRequestDto;
import com.petvission.reserva.dto.ReservaResponseDto;
import com.petvission.reserva.dto.ReservaUsuarioDto;
import com.petvission.reserva.dto.ReprogramarReservaDto;

import com.petvission.reserva.mapper.ReservaMapper;

import com.petvission.reserva.model.EstadoReserva;
import com.petvission.reserva.model.Reserva;

import com.petvission.reserva.repository.ReservaRepository;

import com.petvission.shared.exception.ResourceNotFoundException;

import com.petvission.usuario.model.Usuario;
import com.petvission.usuario.model.UsuarioVeterinario;

import com.petvission.usuario.repository.UsuarioRepository;
import com.petvission.usuario.repository.UsuarioVeterinarioRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;

    private final UsuarioVeterinarioRepository veterinarioRepository;

    private final UsuarioRepository usuarioRepository;

    private final ReservaMapper reservaMapper;

    private final HorarioRepository horarioRepository;

    /*
     * AGENDAR RESERVA
     */
    public Reserva agendarReserva(Reserva reserva) {

        boolean ocupado =
                reservaRepository.existsByVeterinarioAndFechaAndHora(
                        reserva.getVeterinario(),
                        reserva.getFecha(),
                        reserva.getHora()
                );

        if (ocupado) {
            throw new RuntimeException(
                    "El veterinario ya tiene una reserva en ese horario"
            );
        }

        reserva.setEstado(EstadoReserva.PENDIENTE);

        return reservaRepository.save(reserva);
    }

    /*
     * CANCELAR RESERVA
     */
    public ReservaUsuarioDto cancelarReserva(Long idReserva) {

        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Reserva no encontrada")
                );

        reserva.setEstado(EstadoReserva.CANCELADA);

        Reserva saved = reservaRepository.save(reserva);

        return ReservaUsuarioDto.builder()
                .idReserva(saved.getIdReserva())
                .nombreCliente(saved.getUsuario().getNombres())
                .nombreVeterinario(saved.getVeterinario().getUsuario().getNombres())
                .fecha(saved.getFecha())
                .hora(saved.getHora())
                .estado(saved.getEstado())
                .motivo(saved.getMotivo())
                .build();
    }

    /*
     * REPROGRAMAR RESERVA
     */
    public ReservaUsuarioDto reprogramarReserva(
            Long idReserva,
            ReprogramarReservaDto dto
    ) {

        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Reserva no encontrada")
                );

        reserva.setFecha(dto.getNuevaFecha());

        reserva.setHora(dto.getNuevaHora());

        reserva.setEstado(EstadoReserva.REPROGRAMADA);

        Reserva saved = reservaRepository.save(reserva);

        return ReservaUsuarioDto.builder()
                .idReserva(saved.getIdReserva())
                .nombreCliente(saved.getUsuario().getNombres())
                .nombreVeterinario(saved.getVeterinario().getUsuario().getNombres())
                .fecha(saved.getFecha())
                .hora(saved.getHora())
                .estado(saved.getEstado())
                .motivo(saved.getMotivo())
                .build();
    }

    /*
     * OBTENER AGENDA GENERAL
     */
    public List<AgendaVeterinarioDto> obtenerAgendaVeterinarios() {

        List<UsuarioVeterinario> veterinarios =
                veterinarioRepository.findAll();

        List<AgendaVeterinarioDto> response =
                new ArrayList<>();

        for (UsuarioVeterinario veterinario : veterinarios) {

            List<AgendaVeterinarioDto.HorarioDisponibleDto>
                    horarios = generarHorariosDisponibles();

            AgendaVeterinarioDto dto =
                    AgendaVeterinarioDto.builder()
                            .idVeterinario(
                                    veterinario.getIdUsuario()
                            )
                            .nombreVeterinario(
                                    veterinario.getUsuario().getNombres()
                            )
                            .especialidad(
                                    veterinario.getEspecialidad()
                            )
                            .horariosDisponibles(horarios)
                            .build();

            response.add(dto);
        }

        return response;
    }

    /*
     * DISPONIBILIDAD MENSUAL
     */
    public List<ReservaUsuarioDto> obtenerAgendaMensualVeterinario(
            Long idVeterinario
    ) {

        UsuarioVeterinario veterinario =
                veterinarioRepository.findById(idVeterinario)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Veterinario no encontrado"
                                )
                        );

        return reservaRepository
                .findByVeterinarioAndFechaGreaterThanEqualOrderByFechaAscHoraAsc(
                        veterinario,
                        LocalDate.now()
                )
                .stream()
                .map(reserva -> ReservaUsuarioDto.builder()
                        .idReserva(reserva.getIdReserva())
                        .nombreCliente(reserva.getUsuario().getNombres())
                        .nombreVeterinario(reserva.getVeterinario().getUsuario().getNombres())
                        .fecha(reserva.getFecha())
                        .hora(reserva.getHora())
                        .estado(reserva.getEstado())
                        .motivo(reserva.getMotivo())
                        .build()
                )
                .toList();
    }

    /*
     * RESERVAS POR CLIENTE
     */
    public List<ReservaUsuarioDto> obtenerReservasPorUsuario(
            Long idUsuario
    ) {

        List<Reserva> reservas =
                reservaRepository
                        .findByUsuario_IdUsuarioOrderByFechaAscHoraAsc(
                                idUsuario
                        );

        return reservas.stream().map(reserva ->

                ReservaUsuarioDto.builder()
                        .idReserva(reserva.getIdReserva())
                        .nombreCliente(
                                reserva.getUsuario().getNombres()
                        )
                        .nombreVeterinario(
                                reserva.getVeterinario()
                                        .getUsuario()
                                        .getNombres()
                        )
                        .fecha(reserva.getFecha())
                        .hora(reserva.getHora())
                        .estado(reserva.getEstado())
                        .motivo(reserva.getMotivo())
                        .build()

        ).toList();
    }

    /*
     * RESERVAS DEL VETERINARIO
     */
    public List<ReservaUsuarioDto> obtenerReservasVeterinario(
            Long idVeterinario
    ) {

        return reservaRepository
                .findByVeterinario_IdUsuarioOrderByFechaAscHoraAsc(
                        idVeterinario
                )
                .stream()
                .map(reserva -> ReservaUsuarioDto.builder()
                        .idReserva(reserva.getIdReserva())
                        .nombreCliente(reserva.getUsuario().getNombres())
                        .nombreVeterinario(reserva.getVeterinario().getUsuario().getNombres())
                        .fecha(reserva.getFecha())
                        .hora(reserva.getHora())
                        .estado(reserva.getEstado())
                        .motivo(reserva.getMotivo())
                        .build()
                )
                .toList();
    }

    /*
     * DISPONIBILIDAD BÁSICA
     */
    public List<AgendaVeterinarioDto.HorarioDisponibleDto>
    obtenerDisponibilidadBasica() {

        return generarHorariosDisponibles();
    }

    /*
     * AGENDAR RESERVA CON DTO
     */
    public ReservaResponseDto agendarReservaDto(
            ReservaRequestDto dto
    ) {

        Usuario usuario =
                usuarioRepository.findById(dto.getIdUsuario())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Usuario no encontrado"
                                )
                        );

        UsuarioVeterinario veterinario =
                veterinarioRepository.findById(dto.getIdVeterinario())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Veterinario no encontrado"
                                )
                        );

        boolean ocupado =
                reservaRepository.existsByVeterinarioAndFechaAndHora(
                        veterinario,
                        dto.getFecha(),
                        dto.getHora()
                );

        if (ocupado) {
            throw new RuntimeException(
                    "El veterinario ya tiene una reserva en ese horario"
            );
        }

        Reserva reserva = Reserva.builder()
                .usuario(usuario)
                .veterinario(veterinario)
                .fecha(dto.getFecha())
                .hora(dto.getHora())
                .motivo(dto.getMotivo())
                .estado(EstadoReserva.PENDIENTE)
                .build();

        horarioRepository
                .findByVeterinario_IdUsuarioAndDisponibleTrue(
                        dto.getIdVeterinario()
                )
                .stream()
                .filter(h ->
                        h.getFecha().equals(dto.getFecha())
                                && h.getHora().equals(dto.getHora())
                )
                .findFirst()
                .ifPresent(h -> {
                    h.setDisponible(false);
                    horarioRepository.save(h);
                });

        return reservaMapper.toDto(
                reservaRepository.save(reserva)
        );
    }

    /*
     * TODAS LAS RESERVAS
     */
    public List<ReservaUsuarioDto> obtenerTodasLasReservas() {

        return reservaRepository.findAll()
                .stream()
                .map(reserva -> ReservaUsuarioDto.builder()
                        .idReserva(reserva.getIdReserva())
                        .nombreCliente(reserva.getUsuario().getNombres())
                        .nombreVeterinario(reserva.getVeterinario().getUsuario().getNombres())
                        .fecha(reserva.getFecha())
                        .hora(reserva.getHora())
                        .estado(reserva.getEstado())
                        .motivo(reserva.getMotivo())
                        .build()
                )
                .toList();
    }

    /*
     * GENERAR HORARIOS
     */
    private List<AgendaVeterinarioDto.HorarioDisponibleDto>
    generarHorariosDisponibles() {

        List<AgendaVeterinarioDto.HorarioDisponibleDto>
                horarios = new ArrayList<>();

        horarios.add(
                AgendaVeterinarioDto.HorarioDisponibleDto
                        .builder()
                        .fecha(LocalDate.now())
                        .hora(LocalTime.of(9, 0))
                        .build()
        );

        horarios.add(
                AgendaVeterinarioDto.HorarioDisponibleDto
                        .builder()
                        .fecha(LocalDate.now())
                        .hora(LocalTime.of(10, 0))
                        .build()
        );

        horarios.add(
                AgendaVeterinarioDto.HorarioDisponibleDto
                        .builder()
                        .fecha(LocalDate.now())
                        .hora(LocalTime.of(11, 0))
                        .build()
        );

        return horarios;
    }

    /*
     * RESERVAS POR FECHA
     */
    public List<ReservaUsuarioDto> obtenerReservasPorFecha(
            LocalDate fecha
    ) {

        return reservaRepository
                .findByFechaOrderByHoraAsc(fecha)
                .stream()
                .map(reserva -> ReservaUsuarioDto.builder()
                        .idReserva(reserva.getIdReserva())
                        .nombreCliente(reserva.getUsuario().getNombres())
                        .nombreVeterinario(reserva.getVeterinario().getUsuario().getNombres())
                        .fecha(reserva.getFecha())
                        .hora(reserva.getHora())
                        .estado(reserva.getEstado())
                        .motivo(reserva.getMotivo())
                        .build()
                )
                .toList();
    }
}
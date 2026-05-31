package com.petvission.reserva.service;

import lombok.RequiredArgsConstructor;

import com.petvission.turno.model.TurnoDetalle;
import com.petvission.turno.repository.TurnoDetalleRepository;

import com.petvission.mascota.model.Mascota;
import com.petvission.mascota.repository.MascotaRepository;

import com.petvission.servicio.model.Servicio;
import com.petvission.servicio.repository.ServicioRepository;

import com.petvission.reserva.dto.AgendaVeterinarioDto;
import com.petvission.reserva.dto.PacienteVetDto;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioVeterinarioRepository veterinarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final MascotaRepository mascotaRepository;
    private final ServicioRepository servicioRepository;
    private final ReservaMapper reservaMapper;
    private final TurnoDetalleRepository turnoDetalleRepository;

    /*
     * AGENDAR RESERVA CON DTO
     */
    @Transactional
    public ReservaResponseDto agendarReservaDto(ReservaRequestDto dto) {

        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        UsuarioVeterinario veterinario = veterinarioRepository.findById(dto.getIdVeterinario())
                .orElseThrow(() -> new ResourceNotFoundException("Veterinario no encontrado"));

        Mascota mascota = mascotaRepository.findById(dto.getIdMascota())
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada"));

        Servicio servicio = (dto.getIdServicio() != null)
                ? servicioRepository.findById(dto.getIdServicio())
                        .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"))
                : null;

        TurnoDetalle turnoDetalle = turnoDetalleRepository.findById(dto.getIdTurnoDetalle())
                .orElseThrow(() -> new ResourceNotFoundException("Turno detalle no encontrado"));

        if (!turnoDetalle.getDisponible()) {
            throw new RuntimeException("El turno detalle seleccionado ya no está disponible");
        }

        boolean ocupado = reservaRepository.existsByVeterinarioAndFechaAndHora(
                veterinario, dto.getFecha(), dto.getHora()
        );

        if (ocupado) {
            throw new RuntimeException("El veterinario ya tiene una reserva en ese turno");
        }

        Reserva reserva = Reserva.builder()
                .usuario(usuario)
                .veterinario(veterinario)
                .mascota(mascota)
                .servicio(servicio)
                .turnoDetalle(turnoDetalle)
                .categoriaReserva(dto.getCategoriaReserva())
                .fecha(dto.getFecha())
                .hora(dto.getHora())
                .motivo(dto.getMotivo())
                .observaciones(dto.getObservaciones())
                .estado(EstadoReserva.PENDIENTE)
                .build();

        turnoDetalle.setDisponible(false);
        turnoDetalleRepository.save(turnoDetalle);

        return reservaMapper.toDto(reservaRepository.save(reserva));
    }

    /*
     * CANCELAR RESERVA
     */
    @Transactional
    public ReservaUsuarioDto cancelarReserva(Long idReserva) {

        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        reserva.setEstado(EstadoReserva.CANCELADA);

        if (reserva.getTurnoDetalle() != null) {
            reserva.getTurnoDetalle().setDisponible(true);
            turnoDetalleRepository.save(reserva.getTurnoDetalle());
        }

        return reservaMapper.toUsuarioDto(reservaRepository.save(reserva));
    }

    /*
     * CONFIRMAR RESERVA
     */
    public ReservaUsuarioDto confirmarReserva(Long idReserva) {

        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        reserva.setEstado(EstadoReserva.CONFIRMADA);

        return reservaMapper.toUsuarioDto(reservaRepository.save(reserva));
    }

    /*
     * COMPLETAR RESERVA
     */
    public ReservaUsuarioDto completarReserva(Long idReserva) {

        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        reserva.setEstado(EstadoReserva.COMPLETADA);

        return reservaMapper.toUsuarioDto(reservaRepository.save(reserva));
    }

    /*
     * REPROGRAMAR RESERVA
     */
    public ReservaUsuarioDto reprogramarReserva(Long idReserva, ReprogramarReservaDto dto) {

        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        reserva.setFecha(dto.getNuevaFecha());
        reserva.setHora(dto.getNuevaHora());
        reserva.setEstado(EstadoReserva.REPROGRAMADA);

        return reservaMapper.toUsuarioDto(reservaRepository.save(reserva));
    }

    /*
     * TODAS LAS RESERVAS
     */
    public List<ReservaUsuarioDto> obtenerTodasLasReservas() {

        return reservaRepository.findAll()
                .stream()
                .map(reservaMapper::toUsuarioDto)
                .toList();
    }

    /*
     * RESERVAS POR CLIENTE
     */
    public List<ReservaUsuarioDto> obtenerReservasPorUsuario(Long idUsuario) {

        return reservaRepository
                .findByUsuario_IdUsuarioOrderByFechaAscHoraAsc(idUsuario)
                .stream()
                .map(reservaMapper::toUsuarioDto)
                .toList();
    }

    /*
     * RESERVAS DEL VETERINARIO
     */
    public List<ReservaUsuarioDto> obtenerReservasVeterinario(Long idVeterinario) {

        return reservaRepository
                .findByVeterinario_IdUsuarioOrderByFechaAscHoraAsc(idVeterinario)
                .stream()
                .map(reservaMapper::toUsuarioDto)
                .toList();
    }

    /*
     * RESERVAS POR FECHA
     */
    public List<ReservaUsuarioDto> obtenerReservasPorFecha(LocalDate fecha) {

        return reservaRepository.findByFechaOrderByHoraAsc(fecha)
                .stream()
                .map(reservaMapper::toUsuarioDto)
                .toList();
    }

    /*
     * AGENDA MENSUAL VETERINARIO
     */
    public List<ReservaUsuarioDto> obtenerAgendaMensualVeterinario(Long idVeterinario) {

        UsuarioVeterinario veterinario = veterinarioRepository.findById(idVeterinario)
                .orElseThrow(() -> new ResourceNotFoundException("Veterinario no encontrado"));

        return reservaRepository
                .findByVeterinarioAndFechaGreaterThanEqualOrderByFechaAscHoraAsc(
                        veterinario, LocalDate.now()
                )
                .stream()
                .map(reservaMapper::toUsuarioDto)
                .toList();
    }

    /*
     * RESERVAS DEL VETERINARIO HOY
     */
    public List<ReservaUsuarioDto> obtenerReservasVeterinarioHoy(Long idVeterinario) {
        return reservaRepository
                .findByVeterinario_IdUsuarioAndFechaOrderByHoraAsc(idVeterinario, LocalDate.now())
                .stream()
                .map(reservaMapper::toUsuarioDto)
                .toList();
    }

    /*
     * PACIENTES DEL VETERINARIO (mascotas únicas con última visita)
     */
    public List<PacienteVetDto> obtenerPacientesVeterinario(Long idVeterinario) {
        List<Reserva> reservas = reservaRepository
                .findByVeterinario_IdUsuarioOrderByFechaAscHoraAsc(idVeterinario);

        Map<Long, Reserva> ultimas = new LinkedHashMap<>();
        for (Reserva r : reservas) {
            if (r.getMascota() != null) {
                ultimas.put(r.getMascota().getIdMascota(), r);
            }
        }

        return ultimas.values().stream()
                .map(r -> PacienteVetDto.builder()
                        .idMascota(r.getMascota().getIdMascota())
                        .nombreMascota(r.getMascota().getNombre())
                        .especie(r.getMascota().getEspecie())
                        .raza(r.getMascota().getRaza())
                        .nombreDueno(
                                r.getUsuario().getNombres()
                                        + " " +
                                        r.getUsuario().getApellidos()
                        )
                        .ultimaVisita(r.getFecha())
                        .activo(r.getMascota().getEstado())
                        .animalGuia(r.getMascota().getAnimalGuia())
                        .build()
                )
                .toList();
    }

    /*
     * AGENDA GENERAL
     */
    public List<AgendaVeterinarioDto> obtenerAgendaVeterinarios() {

        List<UsuarioVeterinario> veterinarios = veterinarioRepository.findAll();
        List<AgendaVeterinarioDto> response = new ArrayList<>();

        for (UsuarioVeterinario veterinario : veterinarios) {
            response.add(
                    AgendaVeterinarioDto.builder()
                            .idVeterinario(veterinario.getIdUsuario())
                            .nombreVeterinario(
                                    veterinario.getUsuario().getNombres()
                                            + " " +
                                            veterinario.getUsuario().getApellidos()
                            )
                            .especialidad(veterinario.getEspecialidad())
                            .horariosDisponibles(generarHorariosDisponibles())
                            .build()
            );
        }

        return response;
    }

    /*
     * DISPONIBILIDAD BÁSICA
     */
    public List<AgendaVeterinarioDto.HorarioDisponibleDto> obtenerDisponibilidadBasica() {

        return generarHorariosDisponibles();
    }

    /*
     * GENERAR HORARIOS
     */
    private List<AgendaVeterinarioDto.HorarioDisponibleDto> generarHorariosDisponibles() {

        List<AgendaVeterinarioDto.HorarioDisponibleDto> horarios = new ArrayList<>();

        horarios.add(AgendaVeterinarioDto.HorarioDisponibleDto.builder()
                .fecha(LocalDate.now()).hora(LocalTime.of(9, 0)).build());

        horarios.add(AgendaVeterinarioDto.HorarioDisponibleDto.builder()
                .fecha(LocalDate.now()).hora(LocalTime.of(10, 0)).build());

        horarios.add(AgendaVeterinarioDto.HorarioDisponibleDto.builder()
                .fecha(LocalDate.now()).hora(LocalTime.of(11, 0)).build());

        return horarios;
    }
}

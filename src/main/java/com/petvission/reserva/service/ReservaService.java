package com.petvission.reserva.service;

import lombok.RequiredArgsConstructor;

import com.petvission.recordatorio.model.Recordatorio;
import com.petvission.recordatorio.repository.RecordatorioRepository;

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
import com.petvission.shared.exception.UnauthorizedException;

import com.petvission.usuario.model.Rol.NombreRol;
import com.petvission.usuario.model.Usuario;
import com.petvission.usuario.model.UsuarioVeterinario;

import com.petvission.usuario.repository.UsuarioRepository;
import com.petvission.usuario.repository.UsuarioVeterinarioRepository;

import org.springframework.security.core.Authentication;
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
    private final RecordatorioRepository recordatorioRepository;

    // ─── Helpers de autorización ─────────────────────────────────────

    private NombreRol rolDe(Authentication auth) {
        return ((Usuario) auth.getPrincipal()).getRol().getNombreRol();
    }

    private Long idDe(Authentication auth) {
        return ((Usuario) auth.getPrincipal()).getIdUsuario();
    }

    private boolean esAdmin(Authentication auth) {
        return rolDe(auth) == NombreRol.ADMINISTRADOR;
    }

    private boolean esClienteDueno(Reserva reserva, Authentication auth) {
        return rolDe(auth) == NombreRol.CLIENTE
                && reserva.getUsuario().getIdUsuario().equals(idDe(auth));
    }

    private boolean esVetAsignado(Reserva reserva, Authentication auth) {
        return rolDe(auth) == NombreRol.VETERINARIO
                && reserva.getVeterinario().getIdUsuario().equals(idDe(auth));
    }

    // ─── Agendar ─────────────────────────────────────────────────────

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
            throw new IllegalArgumentException("El turno detalle seleccionado ya no está disponible");
        }

        LocalDate hoy = LocalDate.now();
        if (dto.getFecha().isBefore(hoy)
                || (dto.getFecha().isEqual(hoy) && !dto.getHora().isAfter(LocalTime.now()))) {
            throw new IllegalArgumentException("No se puede agendar en un horario que ya pasó");
        }

        boolean ocupado = reservaRepository
                .existsByVeterinario_IdUsuarioAndFechaAndHoraAndEstadoIn(
                        dto.getIdVeterinario(), dto.getFecha(), dto.getHora(),
                        List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA));
        if (ocupado) {
            throw new IllegalArgumentException("El veterinario ya tiene una reserva activa en ese horario");
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

        Reserva saved = reservaRepository.save(reserva);

        recordatorioRepository.save(Recordatorio.builder().reserva(saved).build());

        return reservaMapper.toDto(saved);
    }

    // ─── Cancelar ────────────────────────────────────────────────────

    @Transactional
    public ReservaUsuarioDto cancelarReserva(Long idReserva, Authentication auth) {

        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        EstadoReserva estado = reserva.getEstado();
        if (estado != EstadoReserva.PENDIENTE && estado != EstadoReserva.CONFIRMADA) {
            throw new IllegalStateException(
                    "No se puede cancelar una reserva en estado " + estado);
        }

        // ADMIN: puede cancelar cualquier estado
        // VET asignado: puede cancelar PENDIENTE y CONFIRMADA (incluyendo no-show)
        // CLIENTE dueño: solo puede cancelar PENDIENTE
        boolean autorizado = esAdmin(auth)
                || esVetAsignado(reserva, auth)
                || (estado == EstadoReserva.PENDIENTE && esClienteDueno(reserva, auth));

        if (!autorizado) {
            throw new UnauthorizedException("No tiene permiso para cancelar esta reserva");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);

        if (reserva.getTurnoDetalle() != null) {
            reserva.getTurnoDetalle().setDisponible(true);
            turnoDetalleRepository.save(reserva.getTurnoDetalle());
        }

        return reservaMapper.toUsuarioDto(reservaRepository.save(reserva));
    }

    // ─── Confirmar (interno — invocado por RecordatorioService) ──────

    @Transactional
    public void confirmarReserva(Long idReserva) {

        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new IllegalStateException(
                    "Solo se puede confirmar una reserva PENDIENTE (estado actual: "
                            + reserva.getEstado() + ")");
        }

        reserva.setEstado(EstadoReserva.CONFIRMADA);
        reservaRepository.save(reserva);
    }

    // ─── Completar ───────────────────────────────────────────────────

    @Transactional
    public ReservaUsuarioDto completarReserva(Long idReserva) {

        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new IllegalStateException(
                    "Solo se puede completar una reserva CONFIRMADA (estado actual: "
                            + reserva.getEstado() + ")");
        }

        reserva.setEstado(EstadoReserva.COMPLETADA);
        return reservaMapper.toUsuarioDto(reservaRepository.save(reserva));
    }

    // ─── Reprogramar ─────────────────────────────────────────────────

    @Transactional
    public ReservaUsuarioDto reprogramarReserva(Long idReserva,
                                                ReprogramarReservaDto dto,
                                                Authentication auth) {

        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        EstadoReserva estado = reserva.getEstado();
        if (estado != EstadoReserva.PENDIENTE && estado != EstadoReserva.CONFIRMADA) {
            throw new IllegalStateException(
                    "No se puede reprogramar una reserva en estado " + estado);
        }

        if (!esAdmin(auth) && !esVetAsignado(reserva, auth) && !esClienteDueno(reserva, auth)) {
            throw new UnauthorizedException("No tiene permiso para reprogramar esta reserva");
        }

        if (dto.getFecha() == null || dto.getHora() == null) {
            throw new IllegalArgumentException("Se requiere nueva fecha y hora para reprogramar");
        }

        LocalDate hoy = LocalDate.now();
        if (dto.getFecha().isBefore(hoy)
                || (dto.getFecha().isEqual(hoy) && !dto.getHora().isAfter(LocalTime.now()))) {
            throw new IllegalArgumentException("No se puede reprogramar en un horario que ya pasó");
        }

        // Verificar que el nuevo slot no esté ocupado por otra reserva activa
        boolean nuevoSlotOcupado = reservaRepository.existeSlotOcupadoExcluyendo(
                reserva.getVeterinario().getIdUsuario(),
                dto.getFecha(), dto.getHora(),
                List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA),
                idReserva);
        if (nuevoSlotOcupado) {
            throw new IllegalArgumentException("El horario seleccionado ya está reservado para ese veterinario");
        }

        // Liberar el slot anterior
        if (reserva.getTurnoDetalle() != null) {
            reserva.getTurnoDetalle().setDisponible(true);
            turnoDetalleRepository.save(reserva.getTurnoDetalle());
            reserva.setTurnoDetalle(null);
        }

        reserva.setFecha(dto.getFecha());
        reserva.setHora(dto.getHora());
        reserva.setEstado(EstadoReserva.PENDIENTE);

        return reservaMapper.toUsuarioDto(reservaRepository.save(reserva));
    }

    // ─── Consultas ───────────────────────────────────────────────────

    public List<ReservaUsuarioDto> obtenerTodasLasReservas() {
        return reservaRepository.findAll()
                .stream()
                .map(reservaMapper::toUsuarioDto)
                .toList();
    }

    public List<ReservaUsuarioDto> obtenerReservasPorUsuario(Long idUsuario) {
        return reservaRepository
                .findByUsuario_IdUsuarioOrderByFechaAscHoraAsc(idUsuario)
                .stream()
                .map(reservaMapper::toUsuarioDto)
                .toList();
    }

    public List<ReservaUsuarioDto> obtenerReservasVeterinario(Long idVeterinario) {
        return reservaRepository
                .findByVeterinario_IdUsuarioOrderByFechaAscHoraAsc(idVeterinario)
                .stream()
                .map(reservaMapper::toUsuarioDto)
                .toList();
    }

    public List<ReservaUsuarioDto> obtenerReservasPorFecha(LocalDate fecha) {
        return reservaRepository.findByFechaOrderByHoraAsc(fecha)
                .stream()
                .map(reservaMapper::toUsuarioDto)
                .toList();
    }

    public List<ReservaUsuarioDto> obtenerAgendaMensualVeterinario(Long idVeterinario) {
        UsuarioVeterinario veterinario = veterinarioRepository.findById(idVeterinario)
                .orElseThrow(() -> new ResourceNotFoundException("Veterinario no encontrado"));

        return reservaRepository
                .findByVeterinarioAndFechaGreaterThanEqualOrderByFechaAscHoraAsc(
                        veterinario, LocalDate.now())
                .stream()
                .map(reservaMapper::toUsuarioDto)
                .toList();
    }

    public List<ReservaUsuarioDto> obtenerReservasVeterinarioHoy(Long idVeterinario) {
        return reservaRepository
                .findByVeterinario_IdUsuarioAndFechaOrderByHoraAsc(idVeterinario, LocalDate.now())
                .stream()
                .map(reservaMapper::toUsuarioDto)
                .toList();
    }

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
                        .nombreDueno(r.getUsuario().getNombres() + " " + r.getUsuario().getApellidos())
                        .ultimaVisita(r.getFecha())
                        .activo(r.getMascota().getEstado())
                        .animalGuia(r.getMascota().getAnimalGuia())
                        .build())
                .toList();
    }

    public List<AgendaVeterinarioDto> obtenerAgendaVeterinarios() {
        List<UsuarioVeterinario> veterinarios = veterinarioRepository.findAll();
        List<AgendaVeterinarioDto> response = new ArrayList<>();

        for (UsuarioVeterinario veterinario : veterinarios) {
            response.add(AgendaVeterinarioDto.builder()
                    .idVeterinario(veterinario.getIdUsuario())
                    .nombreVeterinario(
                            veterinario.getUsuario().getNombres()
                                    + " " + veterinario.getUsuario().getApellidos())
                    .especialidad(veterinario.getEspecialidad())
                    .horariosDisponibles(generarHorariosDisponibles())
                    .build());
        }

        return response;
    }

    public List<AgendaVeterinarioDto.HorarioDisponibleDto> obtenerDisponibilidadBasica() {
        return generarHorariosDisponibles();
    }

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

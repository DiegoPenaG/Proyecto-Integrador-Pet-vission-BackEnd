package com.petvission.turno.service;

import com.petvission.reserva.model.EstadoReserva;
import com.petvission.reserva.repository.ReservaRepository;
import com.petvission.shared.exception.ResourceNotFoundException;
import com.petvission.turno.dto.ActualizarDisponibilidadDto;
import com.petvission.turno.dto.GeneracionResponseDto;
import com.petvission.turno.dto.TurnoDetalleResponseDto;
import com.petvission.turno.dto.HorarioPlantillaResponseDto;
import com.petvission.turno.dto.TurnoRequestDto;
import com.petvission.turno.dto.TurnoResponseDto;
import com.petvission.turno.mapper.TurnoMapper;
import com.petvission.turno.model.DiaSemana;
import com.petvission.turno.model.HorarioPlantilla;
import com.petvission.turno.model.TipoTurno;
import com.petvission.turno.model.Turno;
import com.petvission.turno.model.TurnoDetalle;
import com.petvission.turno.repository.HorarioPlantillaRepository;
import com.petvission.turno.repository.TurnoDetalleRepository;
import com.petvission.turno.repository.TurnoRepository;
import com.petvission.usuario.model.UsuarioVeterinario;
import com.petvission.usuario.repository.UsuarioVeterinarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TurnoService {

    private final TurnoRepository turnoRepository;
    private final TurnoDetalleRepository turnoDetalleRepository;
    private final UsuarioVeterinarioRepository veterinarioRepository;
    private final HorarioPlantillaRepository plantillaRepository;
    private final TurnoMapper turnoMapper;
    private final ReservaRepository reservaRepository;

    private static final List<EstadoReserva> ESTADOS_ACTIVOS =
            List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA);

    // Clave de un slot: "YYYY-MM-DD|HH:mm" — usada para comparar ocupación
    private Set<String> slotsOcupados(Long idVeterinario, LocalDate desde) {
        return reservaRepository
                .findByVeterinario_IdUsuarioAndFechaGreaterThanEqualAndEstadoIn(
                        idVeterinario, desde, ESTADOS_ACTIVOS)
                .stream()
                .map(r -> r.getFecha() + "|" + r.getHora())
                .collect(Collectors.toSet());
    }

    private static String clave(TurnoDetalleResponseDto dto) {
        return dto.getFecha() + "|" + dto.getHoraInicio();
    }

    // Slot en el futuro: fecha posterior a hoy, o hoy con hora aún no pasada
    private static boolean esFuturo(TurnoDetalleResponseDto dto) {
        LocalDate hoy = LocalDate.now();
        return dto.getFecha().isAfter(hoy)
                || (dto.getFecha().isEqual(hoy) && dto.getHoraInicio().isAfter(LocalTime.now()));
    }

    /*
     * LISTAR TODOS LOS TURNOS
     */
    public List<TurnoResponseDto> listarTodos() {
        return turnoRepository.findAll()
                .stream()
                .map(turnoMapper::toDto)
                .toList();
    }

    /*
     * LISTAR TURNOS POR VETERINARIO
     */
    public List<TurnoResponseDto> listarPorVeterinario(Long idVeterinario) {
        return turnoRepository.findByVeterinario_IdUsuario(idVeterinario)
                .stream()
                .map(turnoMapper::toDto)
                .toList();
    }

    /*
     * LISTAR PLANTILLAS DE HORARIO POR VETERINARIO
     */
    public List<HorarioPlantillaResponseDto> listarPlantillasPorVeterinario(Long idVeterinario) {
        return plantillaRepository.findByVeterinario_IdUsuario(idVeterinario)
                .stream()
                .map(p -> HorarioPlantillaResponseDto.builder()
                        .id(p.getId())
                        .idVeterinario(p.getVeterinario().getIdUsuario())
                        .nombreVeterinario(
                                p.getVeterinario().getUsuario().getNombres()
                                        + " " +
                                        p.getVeterinario().getUsuario().getApellidos()
                        )
                        .diaSemana(p.getDiaSemana())
                        .horaInicio(p.getHoraInicio())
                        .horaFin(p.getHoraFin())
                        .activo(p.getActivo())
                        .build()
                )
                .toList();
    }

    /*
     * LISTAR TODAS LAS PLANTILLAS DE HORARIO (ADMINISTRADOR)
     */
    @Transactional(readOnly = true)
    public List<HorarioPlantillaResponseDto> listarTodasLasPlantillas() {
        return plantillaRepository.findAll()
                .stream()
                .map(this::toPlantillaDto)
                .toList();
    }

    /*
     * ACTIVAR PLANTILLA DE HORARIO
     */
    @Transactional
    public HorarioPlantillaResponseDto activarPlantilla(Long id) {
        HorarioPlantilla plantilla = plantillaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plantilla no encontrada"));
        plantilla.setActivo(true);
        return toPlantillaDto(plantillaRepository.save(plantilla));
    }

    /*
     * DESACTIVAR PLANTILLA DE HORARIO
     */
    @Transactional
    public HorarioPlantillaResponseDto desactivarPlantilla(Long id) {
        HorarioPlantilla plantilla = plantillaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plantilla no encontrada"));
        plantilla.setActivo(false);
        return toPlantillaDto(plantillaRepository.save(plantilla));
    }

    private HorarioPlantillaResponseDto toPlantillaDto(HorarioPlantilla p) {
        return HorarioPlantillaResponseDto.builder()
                .id(p.getId())
                .idVeterinario(p.getVeterinario().getIdUsuario())
                .nombreVeterinario(
                        p.getVeterinario().getUsuario().getNombres()
                                + " " +
                                p.getVeterinario().getUsuario().getApellidos()
                )
                .diaSemana(p.getDiaSemana())
                .horaInicio(p.getHoraInicio())
                .horaFin(p.getHoraFin())
                .activo(p.getActivo())
                .build();
    }

    /*
     * LISTAR DETALLES DISPONIBLES DE UN TURNO
     */
    public List<TurnoDetalleResponseDto> listarDetallesDisponibles(Long idTurno) {
        return turnoDetalleRepository.findByTurno_IdAndDisponibleTrue(idTurno)
                .stream()
                .map(turnoMapper::toDetalleDto)
                .toList();
    }

    /*
     * DISPONIBILIDAD POR VETERINARIO (todos los días futuros)
     * Fuente de verdad: slot libre = disponible=true en turno_detalle
     * Y no existe reserva PENDIENTE/CONFIRMADA para ese (vet, fecha, hora).
     */
    public List<TurnoDetalleResponseDto> obtenerDisponibilidadVeterinario(Long idVeterinario) {
        Set<String> ocupados = slotsOcupados(idVeterinario, LocalDate.now());

        return turnoDetalleRepository
                .findByTurno_Veterinario_IdUsuarioAndTurno_FechaGreaterThanEqualAndDisponibleTrueOrderByTurno_FechaAscHoraInicioAsc(
                        idVeterinario, LocalDate.now())
                .stream()
                .map(td -> {
                    TurnoDetalleResponseDto dto = turnoMapper.toDetalleDto(td);
                    dto.setFecha(td.getTurno().getFecha());
                    return dto;
                })
                .filter(dto -> !ocupados.contains(clave(dto)))
                .filter(TurnoService::esFuturo)
                .toList();
    }

    /*
     * CREAR TURNO CON SUS DETALLES
     */
    @Transactional
    public TurnoResponseDto crear(TurnoRequestDto dto) {
        UsuarioVeterinario veterinario = veterinarioRepository
                .findById(dto.getIdVeterinario())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Veterinario no encontrado: " + dto.getIdVeterinario())
                );

        if (turnoRepository.existsByVeterinario_IdUsuarioAndFechaAndHoraInicio(
                dto.getIdVeterinario(), dto.getFecha(), dto.getHoraInicio())) {
            throw new IllegalArgumentException(
                    "Ya existe un turno para este veterinario en esa fecha y hora de inicio");
        }

        Turno turno = Turno.builder()
                .veterinario(veterinario)
                .fecha(dto.getFecha())
                .horaInicio(dto.getHoraInicio())
                .horaFin(dto.getHoraFin())
                .tipoTurno(dto.getTipoTurno())
                .activo(true)
                .build();

        Turno turnoGuardado = turnoRepository.save(turno);

        // Filtra detalles que ya existen para este vet/fecha para evitar slots duplicados
        List<TurnoDetalle> detalles = dto.getDetalles().stream()
                .filter(d -> !turnoDetalleRepository
                        .existsByTurno_Veterinario_IdUsuarioAndTurno_FechaAndHoraInicio(
                                dto.getIdVeterinario(), dto.getFecha(), d.getHoraInicio()))
                .map(d -> TurnoDetalle.builder()
                        .turno(turnoGuardado)
                        .horaInicio(d.getHoraInicio())
                        .horaFin(d.getHoraFin())
                        .disponible(true)
                        .build())
                .toList();

        turnoGuardado.setDetalles(turnoDetalleRepository.saveAll(detalles));
        return turnoMapper.toDto(turnoGuardado);
    }

    /*
     * ACTIVAR TURNO
     */
    public TurnoResponseDto activar(Long id) {
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Turno no encontrado: " + id)
                );
        turno.setActivo(true);
        return turnoMapper.toDto(turnoRepository.save(turno));
    }

    /*
     * DESACTIVAR TURNO
     */
    public TurnoResponseDto desactivar(Long id) {
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Turno no encontrado: " + id)
                );
        turno.setActivo(false);
        return turnoMapper.toDto(turnoRepository.save(turno));
    }

    /*
     * DISPONIBILIDAD POR VETERINARIO Y FECHA
     * Misma fuente de verdad que obtenerDisponibilidadVeterinario, acotada a un día.
     */
    public List<TurnoDetalleResponseDto> obtenerDisponibilidadPorFecha(Long idVeterinario, LocalDate fecha) {
        Set<String> ocupados = slotsOcupados(idVeterinario, fecha);

        return turnoDetalleRepository
                .findByTurno_Veterinario_IdUsuarioAndTurno_FechaAndDisponibleTrueOrderByHoraInicioAsc(idVeterinario, fecha)
                .stream()
                .map(td -> {
                    TurnoDetalleResponseDto dto = turnoMapper.toDetalleDto(td);
                    dto.setFecha(fecha);
                    return dto;
                })
                .filter(dto -> !ocupados.contains(clave(dto)))
                .filter(TurnoService::esFuturo)
                .toList();
    }

    /*
     * ACTUALIZAR DISPONIBILIDAD DE UN TURNO DETALLE
     */
    public TurnoDetalleResponseDto actualizarDisponibilidad(Long id, ActualizarDisponibilidadDto dto) {
        TurnoDetalle detalle = turnoDetalleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TurnoDetalle no encontrado: " + id));
        detalle.setDisponible(dto.getDisponible());
        TurnoDetalle guardado = turnoDetalleRepository.save(detalle);
        TurnoDetalleResponseDto response = turnoMapper.toDetalleDto(guardado);
        response.setFecha(guardado.getTurno().getFecha());
        return response;
    }

    /*
     * GENERAR TURNOS DESDE PLANTILLAS
     *
     * Itera los próximos N días, lee las plantillas activas y crea los
     * TurnoDetalle que aún no existan.
     *
     * Turno nocturno (horaFin < horaInicio, ej. 20:00–02:00):
     *   - Parte tarde  → slots 20:00–23:30, fecha = día actual
     *   - Parte madrugada → slots 00:00–01:30, fecha = día siguiente
     *
     * Usa minutos desde medianoche (0–1440) para evitar problemas con
     * LocalTime.MIDNIGHT al cruzar la medianoche.
     */
    @Transactional
    public GeneracionResponseDto generarTurnos(int dias) {
        List<HorarioPlantilla> plantillas = plantillaRepository.findByActivoTrue();
        int generados = 0;
        LocalDate hoy = LocalDate.now();

        for (HorarioPlantilla plantilla : plantillas) {
            UsuarioVeterinario vet = plantilla.getVeterinario();
            boolean cruzaMedianoche = plantilla.getHoraFin().isBefore(plantilla.getHoraInicio());

            for (int i = 0; i < dias; i++) {
                LocalDate fecha = hoy.plusDays(i);
                if (!coincideDia(fecha.getDayOfWeek(), plantilla.getDiaSemana())) continue;

                if (!cruzaMedianoche) {
                    generados += crearSlots(vet, fecha,
                            plantilla.getHoraInicio(), plantilla.getHoraFin());
                } else {
                    // Parte tarde: 20:00 → 23:30  (endMin = 1440 = medianoche)
                    generados += crearSlots(vet, fecha,
                            plantilla.getHoraInicio(), LocalTime.MIDNIGHT);
                    // Parte madrugada: 00:00 → 01:30  (fecha + 1 día)
                    generados += crearSlots(vet, fecha.plusDays(1),
                            LocalTime.MIDNIGHT, plantilla.getHoraFin());
                }
            }
        }

        return new GeneracionResponseDto(generados);
    }

    private int crearSlots(UsuarioVeterinario vet, LocalDate fecha,
                           LocalTime desde, LocalTime hasta) {
        int startMin = desde.toSecondOfDay() / 60;
        // MIDNIGHT = 00:00 = 0 segundos; lo tratamos como 1440 (fin de día)
        int endMin = hasta.equals(LocalTime.MIDNIGHT) ? 1440 : hasta.toSecondOfDay() / 60;

        if (startMin >= endMin) return 0;

        int generados = 0;
        Turno turno = null;

        for (int m = startMin; m < endMin; m += 30) {
            LocalTime slotInicio = LocalTime.ofSecondOfDay((long) m * 60);
            LocalTime slotFin    = (m + 30 == 1440)
                    ? LocalTime.MIDNIGHT
                    : LocalTime.ofSecondOfDay((long) (m + 30) * 60);

            boolean existe = turnoDetalleRepository
                    .existsByTurno_Veterinario_IdUsuarioAndTurno_FechaAndHoraInicio(
                            vet.getIdUsuario(), fecha, slotInicio);

            if (!existe) {
                if (turno == null) {
                    turno = obtenerOCrearTurno(vet, fecha, desde, hasta);
                }
                turnoDetalleRepository.save(TurnoDetalle.builder()
                        .turno(turno)
                        .horaInicio(slotInicio)
                        .horaFin(slotFin)
                        .disponible(true)
                        .build());
                generados++;
            }
        }

        return generados;
    }

    private Turno obtenerOCrearTurno(UsuarioVeterinario vet, LocalDate fecha,
                                     LocalTime horaInicio, LocalTime horaFin) {
        return turnoRepository
                .findByVeterinario_IdUsuarioAndFechaAndHoraInicio(
                        vet.getIdUsuario(), fecha, horaInicio)
                .orElseGet(() -> turnoRepository.save(Turno.builder()
                        .veterinario(vet)
                        .fecha(fecha)
                        .horaInicio(horaInicio)
                        .horaFin(horaFin)
                        .tipoTurno(derivarTipoTurno(horaInicio))
                        .activo(true)
                        .build()));
    }

    private TipoTurno derivarTipoTurno(LocalTime hora) {
        int h = hora.getHour();
        if (h >= 6 && h < 14) return TipoTurno.MANANA;
        if (h >= 14 && h < 20) return TipoTurno.TARDE;
        return TipoTurno.NOCHE;
    }

    private boolean coincideDia(DayOfWeek dow, DiaSemana dia) {
        return switch (dia) {
            case LUNES     -> dow == DayOfWeek.MONDAY;
            case MARTES    -> dow == DayOfWeek.TUESDAY;
            case MIERCOLES -> dow == DayOfWeek.WEDNESDAY;
            case JUEVES    -> dow == DayOfWeek.THURSDAY;
            case VIERNES   -> dow == DayOfWeek.FRIDAY;
            case SABADO    -> dow == DayOfWeek.SATURDAY;
        };
    }
}

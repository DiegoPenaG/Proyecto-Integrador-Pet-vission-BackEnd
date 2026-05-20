package com.petvission.cita.service;

import lombok.RequiredArgsConstructor;
import com.petvission.cita.dto.AgendaVeterinarioDto;
import com.petvission.cita.dto.CitaUsuarioDto;
import com.petvission.cita.dto.ReprogramarCitaDto;
import com.petvission.cita.model.Cita;
import com.petvission.cita.model.EstadoCita;
import com.petvission.cita.repository.CitaRepository;
import com.petvission.shared.exception.ResourceNotFoundException;
import com.petvission.usuario.model.UsuarioVeterinario;
import com.petvission.usuario.repository.UsuarioVeterinarioRepository;
import org.springframework.stereotype.Service;
import com.petvission.cita.dto.CitaRequestDto;
import com.petvission.cita.dto.CitaResponseDto;
import com.petvission.cita.mapper.CitaMapper;
import com.petvission.usuario.model.Usuario;
import com.petvission.usuario.repository.UsuarioRepository;
import com.petvission.horario.repository.HorarioRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CitaService {

    private final CitaRepository citaRepository;

    private final UsuarioVeterinarioRepository veterinarioRepository;

    private final UsuarioRepository usuarioRepository;
    private final CitaMapper citaMapper;

    private final HorarioRepository horarioRepository;

    /*
     * AGENDAR CITA
     */
    public Cita agendarCita(Cita cita) {

        boolean ocupado =
                citaRepository.existsByVeterinarioAndFechaAndHora(
                        cita.getVeterinario(),
                        cita.getFecha(),
                        cita.getHora()
                );

        if (ocupado) {
            throw new RuntimeException(
                    "El veterinario ya tiene una cita en ese horario"
            );
        }

        cita.setEstado(EstadoCita.PENDIENTE);

        return citaRepository.save(cita);
    }


    /*
     * CANCELAR CITA
     */
    public CitaUsuarioDto cancelarCita(Long idCita) {
        Cita cita = citaRepository.findById(idCita)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cita no encontrada")
                );

        cita.setEstado(EstadoCita.CANCELADA);
        Cita saved = citaRepository.save(cita);

        return CitaUsuarioDto.builder()
                .idCita(saved.getIdCita())
                .nombreCliente(saved.getUsuario().getNombres())
                .nombreVeterinario(saved.getVeterinario().getUsuario().getNombres())
                .fecha(saved.getFecha())
                .hora(saved.getHora())
                .estado(saved.getEstado())
                .motivo(saved.getMotivo())
                .build();
    }

    /*
     * REPROGRAMAR CITA
     */
    public CitaUsuarioDto reprogramarCita(Long idCita, ReprogramarCitaDto dto) {
        Cita cita = citaRepository.findById(idCita)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cita no encontrada")
                );

        cita.setFecha(dto.getNuevaFecha());
        cita.setHora(dto.getNuevaHora());
        cita.setEstado(EstadoCita.REPROGRAMADA);
        Cita saved = citaRepository.save(cita);

        return CitaUsuarioDto.builder()
                .idCita(saved.getIdCita())
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
    public List<CitaUsuarioDto> obtenerAgendaMensualVeterinario(
            Long idVeterinario
    ) {
        UsuarioVeterinario veterinario =
                veterinarioRepository.findById(idVeterinario)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Veterinario no encontrado"
                                )
                        );

        return citaRepository
                .findByVeterinarioAndFechaGreaterThanEqualOrderByFechaAscHoraAsc(
                        veterinario,
                        LocalDate.now()
                )
                .stream()
                .map(cita -> CitaUsuarioDto.builder()
                        .idCita(cita.getIdCita())
                        .nombreCliente(cita.getUsuario().getNombres())
                        .nombreVeterinario(cita.getVeterinario().getUsuario().getNombres())
                        .fecha(cita.getFecha())
                        .hora(cita.getHora())
                        .estado(cita.getEstado())
                        .motivo(cita.getMotivo())
                        .build()
                )
                .toList();
    }

    /*
     * CITAS POR CLIENTE
     */
    public List<CitaUsuarioDto> obtenerCitasPorUsuario(
            Long idUsuario
    ) {

        List<Cita> citas =
                citaRepository
                        .findByUsuario_IdUsuarioOrderByFechaAscHoraAsc(
                                idUsuario
                        );

        return citas.stream().map(cita ->

                CitaUsuarioDto.builder()
                        .idCita(cita.getIdCita())
                        .nombreCliente(
                                cita.getUsuario().getNombres()
                        )
                        .nombreVeterinario(
                                cita.getVeterinario()
                                        .getUsuario()
                                        .getNombres()
                        )
                        .fecha(cita.getFecha())
                        .hora(cita.getHora())
                        .estado(cita.getEstado())
                        .motivo(cita.getMotivo())
                        .build()

        ).toList();
    }

    /*
     * CITAS DEL VETERINARIO
     */

    /*
     * CITAS DEL VETERINARIO
     */
    public List<CitaUsuarioDto> obtenerCitasVeterinario(Long idVeterinario) {
        return citaRepository
                .findByVeterinario_IdUsuarioOrderByFechaAscHoraAsc(idVeterinario)
                .stream()
                .map(cita -> CitaUsuarioDto.builder()
                        .idCita(cita.getIdCita())
                        .nombreCliente(cita.getUsuario().getNombres())
                        .nombreVeterinario(cita.getVeterinario().getUsuario().getNombres())
                        .fecha(cita.getFecha())
                        .hora(cita.getHora())
                        .estado(cita.getEstado())
                        .motivo(cita.getMotivo())
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
     * AGENDAR CITA CON DTO
     */
    public CitaResponseDto agendarCitaDto(CitaRequestDto dto) {
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        UsuarioVeterinario veterinario = veterinarioRepository.findById(dto.getIdVeterinario())
                .orElseThrow(() -> new ResourceNotFoundException("Veterinario no encontrado"));

        boolean ocupado = citaRepository.existsByVeterinarioAndFechaAndHora(
                veterinario, dto.getFecha(), dto.getHora()
        );

        if (ocupado) {
            throw new RuntimeException("El veterinario ya tiene una cita en ese horario");
        }

        Cita cita = Cita.builder()
                .usuario(usuario)
                .veterinario(veterinario)
                .fecha(dto.getFecha())
                .hora(dto.getHora())
                .motivo(dto.getMotivo())
                .estado(EstadoCita.PENDIENTE)
                .build();

        // marcar el horario como no disponible
        horarioRepository
                .findByVeterinario_IdUsuarioAndDisponibleTrue(dto.getIdVeterinario())
                .stream()
                .filter(h -> h.getFecha().equals(dto.getFecha()) && h.getHora().equals(dto.getHora()))
                .findFirst()
                .ifPresent(h -> {
                    h.setDisponible(false);
                    horarioRepository.save(h);
                });

        return citaMapper.toDto(citaRepository.save(cita));
    }
    /*
     * TODAS LAS CITAS
     */
    public List<CitaUsuarioDto> obtenerTodasLasCitas() {
        return citaRepository.findAll()
                .stream()
                .map(cita -> CitaUsuarioDto.builder()
                        .idCita(cita.getIdCita())
                        .nombreCliente(cita.getUsuario().getNombres())
                        .nombreVeterinario(cita.getVeterinario().getUsuario().getNombres())
                        .fecha(cita.getFecha())
                        .hora(cita.getHora())
                        .estado(cita.getEstado())
                        .motivo(cita.getMotivo())
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
     * CITAS POR FECHA
     */
    public List<CitaUsuarioDto> obtenerCitasPorFecha(LocalDate fecha) {
        return citaRepository
                .findByFechaOrderByHoraAsc(fecha)
                .stream()
                .map(cita -> CitaUsuarioDto.builder()
                        .idCita(cita.getIdCita())
                        .nombreCliente(cita.getUsuario().getNombres())
                        .nombreVeterinario(cita.getVeterinario().getUsuario().getNombres())
                        .fecha(cita.getFecha())
                        .hora(cita.getHora())
                        .estado(cita.getEstado())
                        .motivo(cita.getMotivo())
                        .build()
                )
                .toList();
    }
}
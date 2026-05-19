package org.example.petvission.cita.service;

import lombok.RequiredArgsConstructor;
import org.example.petvission.cita.dto.AgendaVeterinarioDto;
import org.example.petvission.cita.dto.CitaUsuarioDto;
import org.example.petvission.cita.dto.ReprogramarCitaDto;
import org.example.petvission.cita.model.Cita;
import org.example.petvission.cita.model.EstadoCita;
import org.example.petvission.cita.repository.CitaRepository;
import org.example.petvission.shared.exception.ResourceNotFoundException;
import org.example.petvission.usuario.model.UsuarioVeterinario;
import org.example.petvission.usuario.repository.UsuarioVeterinarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CitaService {

    private final CitaRepository citaRepository;

    private final UsuarioVeterinarioRepository veterinarioRepository;

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
    public Cita cancelarCita(Long idCita) {

        Cita cita = citaRepository.findById(idCita)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cita no encontrada"
                        )
                );

        cita.setEstado(EstadoCita.CANCELADA);

        return citaRepository.save(cita);
    }

    /*
     * REPROGRAMAR CITA
     */
    public Cita reprogramarCita(
            Long idCita,
            ReprogramarCitaDto dto
    ) {

        Cita cita = citaRepository.findById(idCita)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cita no encontrada"
                        )
                );

        cita.setFecha(dto.getNuevaFecha());

        cita.setHora(dto.getNuevaHora());

        cita.setEstado(EstadoCita.REPROGRAMADA);

        return citaRepository.save(cita);
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
                                    veterinario.getIdVeterinario()
                            )
                            .nombreVeterinario(
                                    veterinario.getUsuario().getName()
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
    public List<Cita> obtenerAgendaMensualVeterinario(
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
                );
    }

    /*
     * CITAS POR CLIENTE
     */
    public List<CitaUsuarioDto> obtenerCitasPorUsuario(
            Long idUsuario
    ) {

        List<Cita> citas =
                citaRepository
                        .findByUsuario_IdOrderByFechaAscHoraAsc(
                                idUsuario
                        );

        return citas.stream().map(cita ->

                CitaUsuarioDto.builder()
                        .idCita(cita.getIdCita())
                        .nombreCliente(
                                cita.getUsuario().getName()
                        )
                        .nombreVeterinario(
                                cita.getVeterinario()
                                        .getUsuario()
                                        .getName()
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
    public List<Cita> obtenerCitasVeterinario(
            Long idVeterinario
    ) {

        return citaRepository
                .findByVeterinario_IdVeterinarioOrderByFechaAscHoraAsc(
                        idVeterinario
                );
    }

    /*
     * DISPONIBILIDAD BÁSICA
     */
    public List<AgendaVeterinarioDto.HorarioDisponibleDto>
    obtenerDisponibilidadBasica() {

        return generarHorariosDisponibles();
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

    public List<Cita> obtenerCitasPorFecha(
            LocalDate fecha
    ) {

        return citaRepository
                .findByFechaOrderByHoraAsc(fecha);
    }
}
package com.petvission.reserva.repository;

import com.petvission.reserva.model.EstadoReserva;
import com.petvission.reserva.model.Reserva;

import com.petvission.usuario.model.UsuarioVeterinario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;

import java.util.List;

@Repository
public interface ReservaRepository
        extends JpaRepository<Reserva, Long> {

    /*
     * VALIDAR HORARIO OCUPADO (legado — sin filtro de estado)
     */
    boolean existsByVeterinarioAndFechaAndHora(
            UsuarioVeterinario veterinario,
            LocalDate fecha,
            LocalTime hora
    );

    /*
     * VALIDAR HORARIO OCUPADO (estados activos) — usado en agendar
     */
    boolean existsByVeterinario_IdUsuarioAndFechaAndHoraAndEstadoIn(
            Long idVeterinario,
            LocalDate fecha,
            LocalTime hora,
            List<EstadoReserva> estados
    );

    /*
     * VALIDAR HORARIO OCUPADO excluyendo una reserva — usado en reprogramar
     * Evita que la propia reserva se bloquee a sí misma al cambiar fecha/hora.
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END FROM Reserva r " +
           "WHERE r.veterinario.idUsuario = :idVet " +
           "AND r.fecha = :fecha AND r.hora = :hora " +
           "AND r.estado IN :estados AND r.idReserva <> :excluir")
    boolean existeSlotOcupadoExcluyendo(
            @Param("idVet")    Long idVet,
            @Param("fecha")    LocalDate fecha,
            @Param("hora")     LocalTime hora,
            @Param("estados")  List<EstadoReserva> estados,
            @Param("excluir")  Long excluir
    );

    /*
     * RESERVAS ACTIVAS FUTURAS DEL VETERINARIO — usado para calcular disponibilidad
     */
    List<Reserva> findByVeterinario_IdUsuarioAndFechaGreaterThanEqualAndEstadoIn(
            Long idVeterinario,
            LocalDate desde,
            List<EstadoReserva> estados
    );

    /*
     * RESERVAS DEL VETERINARIO
     */
    List<Reserva>
    findByVeterinario_IdUsuarioOrderByFechaAscHoraAsc(
            Long idVeterinario
    );

    /*
     * RESERVAS DEL USUARIO
     */
    List<Reserva>
    findByUsuario_IdUsuarioOrderByFechaAscHoraAsc(
            Long idUsuario
    );

    /*
     * RESERVAS ACTIVAS DE UNA MASCOTA — usado en reasignación de dueño
     */
    List<Reserva> findByMascota_IdMascotaAndEstadoIn(
            Long idMascota,
            List<EstadoReserva> estados
    );

    /*
     * RESERVAS POR ESTADO
     */
    List<Reserva> findByEstado(
            EstadoReserva estado
    );

    /*
     * RESERVAS POR FECHA
     */
    List<Reserva> findByFechaOrderByHoraAsc(
            LocalDate fecha
    );

    /*
     * RESERVAS ACTIVAS POR FECHA — todos los vets, para calcular disponibilidad multi-vet
     */
    List<Reserva> findByFechaAndEstadoIn(
            LocalDate fecha,
            List<EstadoReserva> estados
    );

    /*
     * AGENDA FUTURA DEL VETERINARIO
     */
    List<Reserva>
    findByVeterinarioAndFechaGreaterThanEqualOrderByFechaAscHoraAsc(
            UsuarioVeterinario veterinario,
            LocalDate fecha
    );

    /*
     * RESERVAS DEL VETERINARIO EN UNA FECHA
     */
    List<Reserva>
    findByVeterinario_IdUsuarioAndFechaOrderByHoraAsc(
            Long idVeterinario,
            LocalDate fecha
    );

    /*
     * RESERVAS VENCIDAS PARA AUTO-CANCEL
     * Selecciona PENDIENTE/CONFIRMADA cuya fecha+hora ya superó el umbral.
     */
    @Query("SELECT r FROM Reserva r WHERE r.estado IN :estados AND " +
           "(r.fecha < :hoy OR (r.fecha = :hoy AND r.hora < :horaLimite))")
    List<Reserva> findReservasVencidas(
            @Param("estados") List<EstadoReserva> estados,
            @Param("hoy") LocalDate hoy,
            @Param("horaLimite") LocalTime horaLimite
    );
}
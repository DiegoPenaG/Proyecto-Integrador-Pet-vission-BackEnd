package com.petvission.reserva.repository;

import com.petvission.reserva.model.EstadoReserva;
import com.petvission.reserva.model.Reserva;

import com.petvission.usuario.model.UsuarioVeterinario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;

import java.util.List;

@Repository
public interface ReservaRepository
        extends JpaRepository<Reserva, Long> {

    /*
     * VALIDAR HORARIO OCUPADO
     */
    boolean existsByVeterinarioAndFechaAndHora(
            UsuarioVeterinario veterinario,
            LocalDate fecha,
            LocalTime hora
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
     * AGENDA FUTURA DEL VETERINARIO
     */
    List<Reserva>
    findByVeterinarioAndFechaGreaterThanEqualOrderByFechaAscHoraAsc(
            UsuarioVeterinario veterinario,
            LocalDate fecha
    );
}
package com.petvission.turno.repository;

import com.petvission.turno.model.TurnoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface TurnoDetalleRepository extends JpaRepository<TurnoDetalle, Long> {

    List<TurnoDetalle> findByTurno_Id(Long idTurno);

    List<TurnoDetalle> findByTurno_IdAndDisponibleTrue(Long idTurno);

    List<TurnoDetalle> findByTurno_Veterinario_IdUsuarioAndDisponibleTrue(Long idVeterinario);

    List<TurnoDetalle> findByTurno_Veterinario_IdUsuarioAndTurno_FechaGreaterThanEqualAndDisponibleTrue(
            Long idVeterinario, LocalDate desde);

    List<TurnoDetalle> findByTurno_Veterinario_IdUsuarioAndTurno_FechaAndDisponibleTrue(
            Long idVeterinario, LocalDate fecha);

    boolean existsByTurno_Veterinario_IdUsuarioAndTurno_FechaAndHoraInicio(
            Long idVeterinario, LocalDate fecha, LocalTime horaInicio);
}
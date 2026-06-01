package com.petvission.turno.repository;

import com.petvission.turno.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {

    List<Turno> findByVeterinario_IdUsuario(Long idVeterinario);

    List<Turno> findByVeterinario_IdUsuarioAndActivoTrue(Long idVeterinario);

    Optional<Turno> findByVeterinario_IdUsuarioAndFechaAndHoraInicio(
            Long idVeterinario, LocalDate fecha, LocalTime horaInicio);

    boolean existsByVeterinario_IdUsuarioAndFechaAndHoraInicio(
            Long idVeterinario, LocalDate fecha, LocalTime horaInicio);
}

package com.petvission.turno.repository;

import com.petvission.turno.model.DiaSemana;
import com.petvission.turno.model.HorarioPlantilla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface HorarioPlantillaRepository extends JpaRepository<HorarioPlantilla, Long> {

    List<HorarioPlantilla> findByActivoTrue();

    List<HorarioPlantilla> findByVeterinario_IdUsuario(Long idVeterinario);

    boolean existsByVeterinario_IdUsuarioAndDiaSemanaAndHoraInicio(
            Long idVeterinario, DiaSemana diaSemana, LocalTime horaInicio);
}

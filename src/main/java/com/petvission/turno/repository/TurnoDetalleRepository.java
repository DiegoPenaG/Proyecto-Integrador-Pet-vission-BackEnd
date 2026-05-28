package com.petvission.turno.repository;

import com.petvission.turno.model.TurnoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurnoDetalleRepository extends JpaRepository<TurnoDetalle, Long> {

    List<TurnoDetalle> findByTurno_Id(Long idTurno);

    List<TurnoDetalle> findByTurno_IdAndDisponibleTrue(Long idTurno);

    List<TurnoDetalle> findByTurno_Veterinario_IdUsuarioAndDisponibleTrue(Long idVeterinario);
}
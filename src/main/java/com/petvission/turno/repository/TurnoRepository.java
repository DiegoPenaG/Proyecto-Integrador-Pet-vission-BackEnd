package com.petvission.turno.repository;

import com.petvission.turno.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {

    List<Turno> findByVeterinario_IdUsuario(Long idVeterinario);

    List<Turno> findByVeterinario_IdUsuarioAndActivoTrue(Long idVeterinario);
}

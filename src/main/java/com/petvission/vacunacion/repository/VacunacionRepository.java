package com.petvission.vacunacion.repository;

import com.petvission.vacunacion.model.Vacunacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacunacionRepository
        extends JpaRepository<Vacunacion, Long> {

    /*
     * VACUNACIONES POR HISTORIAL CLÍNICO
     */
    List<Vacunacion> findByHistorialClinico_IdHistorial(Long idHistorial);
}
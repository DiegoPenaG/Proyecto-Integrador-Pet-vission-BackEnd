package com.petvission.vacunacion.repository;

import com.petvission.vacunacion.model.Vacunacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacunacionRepository
        extends JpaRepository<Vacunacion, Long> {

    List<Vacunacion> findByHistorialClinico_IdHistorial(Long idHistorial);

    List<Vacunacion> findByMascota_IdMascotaOrderByFechaAplicacionDesc(Long idMascota);
}
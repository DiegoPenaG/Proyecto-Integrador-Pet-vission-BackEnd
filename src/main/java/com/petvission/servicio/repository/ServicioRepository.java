package com.petvission.servicio.repository;

import com.petvission.servicio.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicioRepository
        extends JpaRepository<Servicio, Integer> {

    /* Obtiene todos los servicios que estén activos */
    List<Servicio> findByActivoTrue();

    /* Obtiene servicios filtrados por categoría */
    List<Servicio> findByCategoria(String categoria);
}

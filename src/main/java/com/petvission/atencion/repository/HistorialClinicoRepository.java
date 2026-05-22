package com.petvission.atencion.repository;

import com.petvission.atencion.model.HistorialClinico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialClinicoRepository
        extends JpaRepository<HistorialClinico, Long> {

    /*
     * HISTORIAL POR MASCOTA
     */
    List<HistorialClinico>
    findByMascota_IdMascotaOrderByFechaRegistroDesc(
            Long idMascota
    );
}
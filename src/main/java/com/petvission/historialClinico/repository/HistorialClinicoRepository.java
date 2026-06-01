package com.petvission.historialClinico.repository;

import com.petvission.historialClinico.model.HistorialClinico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialClinicoRepository
        extends JpaRepository<HistorialClinico, Long> {
    /*
     *  Historial por id
     */
    List<HistorialClinico> findByIdHistorial(Long id);

    /*
     * HISTORIAL POR MASCOTA
     */
    List<HistorialClinico>
    findByMascota_IdMascotaOrderByFechaRegistroDesc(
            Long idMascota
    );
}
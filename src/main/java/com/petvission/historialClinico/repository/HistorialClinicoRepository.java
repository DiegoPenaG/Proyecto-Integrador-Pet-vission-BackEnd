package com.petvission.historialClinico.repository;

import com.petvission.historialClinico.model.HistorialClinico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

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

    /*
     * HISTORIAL POR RESERVA (para modo edición en la ficha)
     */
    Optional<HistorialClinico> findByReserva_IdReserva(Long idReserva);
}
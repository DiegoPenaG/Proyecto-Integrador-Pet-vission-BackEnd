package com.petvission.atencion.repository;

import com.petvission.atencion.model.Tratamiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TratamientoRepository
        extends JpaRepository<Tratamiento, Long> {

    /*
     * TRATAMIENTOS POR HISTORIAL CLÍNICO
     */
    List<Tratamiento>
    findByHistorialClinico_IdHistorial(
            Long idHistorial
    );
}
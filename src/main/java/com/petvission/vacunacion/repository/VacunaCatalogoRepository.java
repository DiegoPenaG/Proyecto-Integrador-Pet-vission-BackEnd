package com.petvission.vacunacion.repository;

import com.petvission.vacunacion.model.VacunaCatalogo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacunaCatalogoRepository
        extends JpaRepository<VacunaCatalogo, Long> {

    @Query("SELECT v FROM VacunaCatalogo v WHERE v.especie = :especie OR v.especie IS NULL ORDER BY v.nombre")
    List<VacunaCatalogo> findByEspecieOrGeneral(@Param("especie") String especie);
}
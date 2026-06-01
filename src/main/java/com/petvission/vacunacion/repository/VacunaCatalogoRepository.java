package com.petvission.vacunacion.repository;

import com.petvission.vacunacion.model.VacunaCatalogo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VacunaCatalogoRepository
        extends JpaRepository<VacunaCatalogo, Long> {
}
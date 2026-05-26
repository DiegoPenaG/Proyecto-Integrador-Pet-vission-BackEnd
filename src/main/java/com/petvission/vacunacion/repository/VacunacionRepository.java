package com.petvission.vacunacion.repository;

import com.petvission.vacunacion.model.Vacunacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VacunacionRepository
        extends JpaRepository<Vacunacion, Long> {
}
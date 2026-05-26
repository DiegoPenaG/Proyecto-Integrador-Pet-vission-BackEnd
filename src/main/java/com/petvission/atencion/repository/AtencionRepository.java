package com.petvission.atencion.repository;

import com.petvission.atencion.model.Atencion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AtencionRepository
        extends JpaRepository<Atencion, Long> {
}
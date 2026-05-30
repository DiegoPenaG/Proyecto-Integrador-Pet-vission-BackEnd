package com.petvission.servicio.repository;

import com.petvission.servicio.model.Servicio;
import com.petvission.servicio.model.TipoServicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicioRepository extends JpaRepository<Servicio, Integer> {

    List<Servicio> findByActivoTrue();

    List<Servicio> findByActivoTrueAndTipoServicio(TipoServicio tipoServicio);
}

package com.example.demo.usuario.repository;

/*
 * =========================================================
 * REPOSITORIO DE ROL
 * ---------------------------------------------------------
 * INTERFAZ ENCARGADA DE LAS OPERACIONES CRUD
 * PARA LA ENTIDAD ROL.
 * =========================================================
 */

import com.example.demo.usuario.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {

    /*
     * =========================================================
     * BUSCAR ROL POR NOMBRE
     * =========================================================
     */
    Optional<Rol> findByNombreRol(String nombreRol);

}
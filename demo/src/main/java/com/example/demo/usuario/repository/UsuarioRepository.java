package com.example.demo.usuario.repository;

/*
 * =========================================================
 * REPOSITORIO DE USUARIO
 * ---------------------------------------------------------
 * INTERFAZ ENCARGADA DE LAS OPERACIONES CRUD
 * PARA LA ENTIDAD USUARIO.
 * =========================================================
 */

import com.example.demo.usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /*
     * =========================================================
     * BUSCAR USUARIO POR CORREO
     * =========================================================
     */
    Optional<Usuario> findByCorreo(String correo);

}
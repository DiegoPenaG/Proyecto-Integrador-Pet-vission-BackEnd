package com.petvission.usuario.repository;

/*
 * =========================================================
 * REPOSITORIO DE USUARIO VETERINARIO
 * ---------------------------------------------------------
 * INTERFAZ ENCARGADA DE LAS OPERACIONES CRUD
 * PARA LA ENTIDAD USUARIOVETERINARIO.
 * =========================================================
 */

import com.petvission.usuario.model.UsuarioVeterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface UsuarioVeterinarioRepository extends JpaRepository<UsuarioVeterinario, Long> {

    Optional<UsuarioVeterinario> findByUsuario_IdUsuario(Long idUsuario);
}
package com.petvission.usuario.repository;

import com.petvission.usuario.model.Usuario;
import com.petvission.usuario.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    List<Usuario> findByEstadoTrue();

    List<Usuario> findByRol_NombreRol(Rol.NombreRol nombreRol);
}
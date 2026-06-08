package com.petvission.notificacion.repository;

import com.petvission.notificacion.model.UsuarioNotificacionPref;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioNotificacionPrefRepository extends JpaRepository<UsuarioNotificacionPref, Long> {

    Optional<UsuarioNotificacionPref> findByUsuario_IdUsuario(Long idUsuario);
}

package com.petvission.notificacion.repository;

import com.petvission.notificacion.model.CitaNotificacionLog;
import com.petvission.notificacion.model.EstadoNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitaNotificacionLogRepository extends JpaRepository<CitaNotificacionLog, Long> {

    List<CitaNotificacionLog> findByCita_IdReserva(Long idReserva);

    boolean existsByCita_IdReservaAndEstado(Long idReserva, EstadoNotificacion estado);
}

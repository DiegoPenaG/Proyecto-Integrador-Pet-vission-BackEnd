package com.petvission.recordatorio.repository;

import com.petvission.recordatorio.model.Recordatorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RecordatorioRepository extends JpaRepository<Recordatorio, Long> {

    Optional<Recordatorio> findByReserva_IdReserva(Long idReserva);
}

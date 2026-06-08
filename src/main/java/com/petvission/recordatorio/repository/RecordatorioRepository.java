package com.petvission.recordatorio.repository;

import com.petvission.recordatorio.model.Recordatorio;
import com.petvission.reserva.model.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecordatorioRepository extends JpaRepository<Recordatorio, Long> {

    Optional<Recordatorio> findByReserva_IdReserva(Long idReserva);

    Optional<Recordatorio> findByReserva_IdReservaAndConfirmationToken(Long idReserva, String token);

    @Query("""
            SELECT r FROM Recordatorio r
            JOIN FETCH r.reserva res
            JOIN FETCH res.usuario u
            JOIN FETCH res.mascota m
            JOIN FETCH res.veterinario v
            JOIN FETCH v.usuario vu
            WHERE r.enviado = false
              AND res.fecha = :fecha
              AND res.estado IN :estados
            """)
    List<Recordatorio> findPendientesParaFecha(
            @Param("fecha") LocalDate fecha,
            @Param("estados") List<EstadoReserva> estados);
}

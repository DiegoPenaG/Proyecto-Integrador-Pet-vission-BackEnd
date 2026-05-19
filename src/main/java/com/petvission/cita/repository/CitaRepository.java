package com.petvission.cita.repository;

import com.petvission.cita.model.Cita;
import com.petvission.cita.model.EstadoCita;
import com.petvission.usuario.model.UsuarioVeterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    boolean existsByVeterinarioAndFechaAndHora(
            UsuarioVeterinario veterinario,
            LocalDate fecha,
            LocalTime hora
    );

    // ✅ idUsuario en vez de Id
    List<Cita> findByVeterinario_IdUsuarioOrderByFechaAscHoraAsc(
            Long idVeterinario
    );

    // ✅ idUsuario en vez de Id
    List<Cita> findByUsuario_IdUsuarioOrderByFechaAscHoraAsc(
            Long idUsuario
    );

    List<Cita> findByEstado(EstadoCita estado);

    List<Cita> findByFechaOrderByHoraAsc(LocalDate fecha);

    List<Cita> findByVeterinarioAndFechaGreaterThanEqualOrderByFechaAscHoraAsc(
            UsuarioVeterinario veterinario,
            LocalDate fecha
    );
}
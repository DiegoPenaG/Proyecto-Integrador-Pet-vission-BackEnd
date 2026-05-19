package com.petvission.cita.repository;

import com.petvission.cita.model.Cita;
import com.petvission.cita.model.EstadoCita;
import com.petvission.usuario.model.Usuario;
import com.petvission.usuario.model.UsuarioVeterinario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    /*
     * VALIDAR HORARIO OCUPADO
     */
    boolean existsByVeterinarioAndFechaAndHora(
            UsuarioVeterinario veterinario,
            LocalDate fecha,
            LocalTime hora
    );

    /*
     * CITAS POR VETERINARIO
     */
    List<Cita> findByVeterinario_IdVeterinarioOrderByFechaAscHoraAsc(
            Long idVeterinario
    );

    /*
     * CITAS POR USUARIO
     */
    List<Cita> findByUsuario_IdOrderByFechaAscHoraAsc(
            Long idUsuario
    );

    /*
     * CITAS POR ESTADO
     */
    List<Cita> findByEstado(EstadoCita estado);

    /*
     * CITAS DE UN DÍA
     */
    List<Cita> findByFechaOrderByHoraAsc(LocalDate fecha);

    /*
     * CITAS FUTURAS VETERINARIO
     */
    List<Cita> findByVeterinarioAndFechaGreaterThanEqualOrderByFechaAscHoraAsc(
            UsuarioVeterinario veterinario,
            LocalDate fecha
    );
}
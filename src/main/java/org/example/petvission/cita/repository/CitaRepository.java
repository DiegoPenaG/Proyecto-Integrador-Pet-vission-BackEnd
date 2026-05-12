package org.example.petvission.cita.repository; // Ajusta el package si es org o com según tu proyecto

import org.example.petvission.cita.model.Cita;
import org.example.petvission.usuario.model.UsuarioVeterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
    // Esta es la relación técnica que definiste para agendar
    boolean existsByVeterinarioAndFechaAndHora(UsuarioVeterinario veterinario, LocalDate fecha, LocalTime hora);
}
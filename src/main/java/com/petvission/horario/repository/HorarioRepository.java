package com.petvission.horario.repository;

import com.petvission.horario.model.Horario;
import com.petvission.usuario.model.UsuarioVeterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HorarioRepository
        extends JpaRepository<Horario, Long> {

    List<Horario> findByVeterinario(
            UsuarioVeterinario veterinario
    );

    List<Horario> findByFecha(LocalDate fecha);

    List<Horario> findByDisponibleTrue();

    List<Horario> findByVeterinarioAndDisponibleTrue(
            UsuarioVeterinario veterinario
    );
}
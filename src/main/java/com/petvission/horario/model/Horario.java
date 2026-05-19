package com.petvission.horario.model;

import jakarta.persistence.*;
import lombok.*;
import com.petvission.usuario.model.UsuarioVeterinario;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "horario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;

    private LocalTime hora;

    @ManyToOne
    @JoinColumn(name = "id_veterinario", nullable = false)
    private UsuarioVeterinario veterinario;

    @Builder.Default
    private Boolean disponible = true;
}
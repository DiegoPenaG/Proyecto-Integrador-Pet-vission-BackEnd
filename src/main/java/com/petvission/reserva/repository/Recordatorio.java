package com.petvission.reserva.repository;

import com.petvission.usuario.model.Usuario;
import com.petvission.reserva.model.Reserva;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

// Funcionalidad completa pendiente para Sprint 3
// Por ahora solo define la entidad base

@Entity
@Table(name = "recordatorio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recordatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRecordatorio;

    private LocalDate fecha;

    private LocalTime hora;

    @Builder.Default
    private Boolean estado = false;

    @ManyToOne
    @JoinColumn(name = "id_cita", nullable = false)
    private Reserva cita;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
}
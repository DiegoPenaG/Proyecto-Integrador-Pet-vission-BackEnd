package com.petvission.reserva.model;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.petvission.usuario.model.Usuario;
import com.petvission.usuario.model.UsuarioVeterinario;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reserva")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Long idReserva;

    /*
     * CLIENTE
     */
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    /*
     * VETERINARIO
     */
    @ManyToOne
    @JoinColumn(name = "id_veterinario", nullable = false)
    private UsuarioVeterinario veterinario;

    /*
     * FECHA
     */
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    /*
     * HORA
     */
    @Column(name = "hora", nullable = false)
    private LocalTime hora;

    /*
     * MOTIVO
     */
    @Column(name = "motivo")
    private String motivo;

    /*
     * ESTADO
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoReserva estado;
}
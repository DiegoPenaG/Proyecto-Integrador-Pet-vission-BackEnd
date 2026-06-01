package com.petvission.reserva.model;

import jakarta.persistence.*;

import lombok.*;

import com.petvission.mascota.model.Mascota;
import com.petvission.servicio.model.Servicio;
import com.petvission.turno.model.TurnoDetalle;
import com.petvission.usuario.model.Usuario;
import com.petvission.usuario.model.UsuarioVeterinario;
import com.petvission.reserva.model.CategoriaReserva;

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
     * SERVICIO (nullable — CONSULTA no requiere servicio específico)
     */
    @ManyToOne
    @JoinColumn(name = "id_servicio", nullable = true)
    private Servicio servicio;

    /*
     * MASCOTA
     */
    @ManyToOne
    @JoinColumn(name = "id_mascota")
    private Mascota mascota;

    /*
     * TURNO DETALLE (SLOT HORARIO RESERVADO)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_turno_detalle")
    private TurnoDetalle turnoDetalle;

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
     * OBSERVACIONES
     */
    @Column(name = "observaciones")
    private String observaciones;


    /*
     * CATEGORÍA DE LA RESERVA
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "categoria_reserva", length = 20)
    private CategoriaReserva categoriaReserva;

    /*
     * ESTADO
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoReserva estado;
}
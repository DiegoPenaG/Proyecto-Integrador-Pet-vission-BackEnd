package com.petvission.historialClinico.model;

import com.petvission.mascota.model.Mascota;
import com.petvission.reserva.model.Reserva;
import com.petvission.usuario.model.UsuarioVeterinario;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "historial_clinico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"mascota", "veterinario"})
@EqualsAndHashCode(exclude = {"mascota", "veterinario"})
public class HistorialClinico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Long idHistorial;

    /*
     * MASCOTA
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mascota", nullable = false)
    private Mascota mascota;

    /*
     * VETERINARIO
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_veterinario", nullable = false)
    private UsuarioVeterinario veterinario;

    /*
     * DIAGNÓSTICO
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String diagnostico;

    /*
     * TRATAMIENTO
     */
    @Column(columnDefinition = "TEXT")
    private String tratamiento;

    /*
     * OBSERVACIONES
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String observaciones;

    /*
     * PESO ACTUAL
     */
    private BigDecimal peso;

    /*
     * FECHA DEL REGISTRO
     */
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    /*
     * RESERVA ASOCIADA (opcional — vincula el historial a la cita)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reserva", nullable = true)
    private Reserva reserva;

    /*
     * SIGNOS VITALES
     */
    @Column(name = "temperatura", precision = 4, scale = 1)
    private java.math.BigDecimal temperatura;

    @Column(name = "frecuencia_cardiaca")
    private Integer frecuenciaCardiaca;

    @Column(name = "frecuencia_respiratoria")
    private Integer frecuenciaRespiratoria;

    @Column(name = "saturacion_oxigeno")
    private Integer saturacionOxigeno;

    @OneToMany(mappedBy = "historialClinico",
            cascade = CascadeType.ALL)
    private List<Tratamiento> tratamientos;

    /*
     * RECETA
     */
    @Column(columnDefinition = "TEXT")
    private String receta;
}


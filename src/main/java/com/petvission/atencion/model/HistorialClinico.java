package com.petvission.atencion.model;

import com.petvission.mascota.model.Mascota;
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

    @OneToMany(mappedBy = "historialClinico",
            cascade = CascadeType.ALL)
    private List<Tratamiento> tratamientos;
}
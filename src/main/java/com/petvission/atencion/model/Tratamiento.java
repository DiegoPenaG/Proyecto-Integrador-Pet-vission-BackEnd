package com.petvission.atencion.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tratamiento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tratamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tratamiento")
    private Long idTratamiento;

    /*
     * HISTORIAL CLÍNICO ASOCIADO
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_historial", nullable = false)
    private HistorialClinico historialClinico;

    /*
     * DESCRIPCIÓN DEL TRATAMIENTO
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    /*
     * DURACIÓN DEL TRATAMIENTO
     */
    private String duracion;

    /*
     * INDICACIONES
     */
    @Column(columnDefinition = "TEXT")
    private String indicaciones;
}
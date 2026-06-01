package com.petvission.servicio.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "servicio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio")
    private Integer idServicio;

    @Column(nullable = false, length = 100)
    private String nombre;

    /* Agrupa el servicio para el wizard: VACUNACION o LABORATORIO.
       Null para servicios que no aplican (p.ej. futuros servicios genéricos). */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_servicio", length = 20)
    private TipoServicio tipoServicio;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "duracion_minutos", nullable = false)
    private Integer duracionMinutos;

    /* Nullable — se muestra como "Consultar precio" en el frontend. */
    @Column
    private Double precio;

    @Column(nullable = false)
    private Boolean activo;
}

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

    /*
     * NOMBRE DEL SERVICIO
     */
    @Column(nullable = false, length = 100)
    private String nombre;

    /*
     * CATEGORÍA DEL SERVICIO (clínico / no clínico)
     */
    @Column(nullable = false, length = 50)
    private String categoria;

    /*
     * DESCRIPCIÓN DETALLADA DEL SERVICIO
     */
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /*
     * DURACIÓN ESTIMADA EN MINUTOS
     */
    @Column(name = "duracion_minutos", nullable = false)
    private Integer duracionMinutos;

    /*
     * PRECIO DEL SERVICIO
     */
    @Column(nullable = false)
    private Double precio;

    /*
     * INDICA SI EL SERVICIO ESTÁ DISPONIBLE
     */
    @Column(nullable = false)
    private Boolean activo;
}

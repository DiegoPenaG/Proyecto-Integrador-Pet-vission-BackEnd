package com.petvission.vacunacion.model;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vacuna_catalogo")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacunaCatalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vacuna")
    private Long idVacuna;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "dosis")
    private String dosis;

    // "CANINA", "FELINA", null = aplica a todas las especies
    @Column(name = "especie")
    private String especie;
}
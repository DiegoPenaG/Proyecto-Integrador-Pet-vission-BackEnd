package com.petvission.vacunacion.model;

import com.petvission.atencion.model.Atencion;
import com.petvission.mascota.model.Mascota;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "vacunacion")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vacunacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vacunacion")
    private Long idVacunacion;

    @ManyToOne
    @JoinColumn(name = "id_mascota", nullable = false)
    private Mascota mascota;

    @ManyToOne
    @JoinColumn(name = "id_vacuna", nullable = false)
    private VacunaCatalogo vacuna;

    /*
     * ATENCIÓN MÉDICA ASOCIADA
     */
    @ManyToOne
    @JoinColumn(name = "id_atencion", nullable = false)
    private Atencion atencion;

    @Column(name = "fecha_aplicacion", nullable = false)
    private LocalDate fechaAplicacion;

    @Column(name = "proxima_dosis")
    private LocalDate proximaDosis;
}
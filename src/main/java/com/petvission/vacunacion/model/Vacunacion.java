package com.petvission.vacunacion.model;

import com.petvission.historialClinico.model.HistorialClinico;
import com.petvission.mascota.model.Mascota;
import com.petvission.usuario.model.UsuarioVeterinario;

import jakarta.persistence.*;

import lombok.*;

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

    /*
     * MASCOTA
     */
    @ManyToOne
    @JoinColumn(name = "id_mascota", nullable = false)
    private Mascota mascota;

    /*
     * VACUNA DEL CATÁLOGO
     */
    @ManyToOne
    @JoinColumn(name = "id_vacuna", nullable = false)
    private VacunaCatalogo vacuna;

    /*
     * HISTORIAL CLÍNICO ASOCIADO
     */
    @ManyToOne
    @JoinColumn(name = "id_historial_clinico", nullable = false) // ← snake_case
    private HistorialClinico historialClinico;

    /*
     * VETERINARIO QUE APLICÓ LA VACUNA
     */
    @ManyToOne
    @JoinColumn(name = "id_veterinario", nullable = false)
    private UsuarioVeterinario veterinario;

    /*
     * FECHA DE APLICACIÓN
     */
    @Column(name = "fecha_aplicacion", nullable = false)
    private LocalDate fechaAplicacion;

    /*
     * PRÓXIMA DOSIS
     */
    @Column(name = "proxima_dosis")
    private LocalDate proximaDosis;

    /*
     * LOTE DE LA VACUNA
     */
    @Column(name = "lote", length = 100)
    private String lote;
}
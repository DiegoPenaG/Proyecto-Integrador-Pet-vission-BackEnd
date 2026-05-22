package com.petvission.mascota.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.petvission.atencion.model.HistorialClinico;
import com.petvission.usuario.model.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "mascota")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMascota;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String especie;

    private String raza;
    private String sexo;
    private LocalDate fechaNacimiento;
    private String color;

    // BigDecimal en vez de Double para precisión decimal
    private BigDecimal peso;

    @Column(nullable = false)
    @Builder.Default
    private Boolean estado = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    /*
     * HISTORIAL CLINICO DE LA MASCOTA
     */
    @JsonIgnore
    @OneToMany(mappedBy = "mascota", cascade = CascadeType.ALL)
    private List<HistorialClinico> historialesClinicos;
}
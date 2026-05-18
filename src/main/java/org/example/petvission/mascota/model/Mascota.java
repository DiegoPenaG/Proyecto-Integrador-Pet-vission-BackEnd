package org.example.petvission.mascota.model;

import org.example.petvission.usuario.model.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/*
 * ENTIDAD MASCOTA
 * REPRESENTA LAS MASCOTAS REGISTRADAS EN EL SISTEMA.
 * CONTIENE LA INFORMACIÓN GENERAL DE CADA MASCOTA
 * Y SU RELACIÓN CON EL USUARIO PROPIETARIO.
 */

@Entity
@Table(name = "mascota")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mascota {

    /*
     * IDENTIFICADOR ÚNICO DE LA MASCOTA
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMascota;

    /*
     * NOMBRE DE LA MASCOTA
     */
    @Column(nullable = false)
    private String nombre;

    /*
     * ESPECIE DE LA MASCOTA
     */
    @Column(nullable = false)
    private String especie;

    /*
     * INFORMACIÓN COMPLEMENTARIA
     * DE LA MASCOTA
     */
    private String raza;
    private String sexo;
    private LocalDate fechaNacimiento;
    private String color;
    private Double peso;

    /*
     * ESTADO LÓGICO DE LA MASCOTA
     * TRUE = ACTIVO
     * FALSE = INACTIVO
     */
    @Column(nullable = false)
    private Boolean estado = true;

    /*
     * RELACIÓN MUCHAS MASCOTAS -> UN USUARIO
     * CADA MASCOTA PERTENECE A UN USUARIO
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
}

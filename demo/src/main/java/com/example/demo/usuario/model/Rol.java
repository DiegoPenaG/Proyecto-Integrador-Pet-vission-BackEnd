package com.example.demo.usuario.model;

/*
 * =========================================================
 * ROL.JAVA
 * ---------------------------------------------------------
 * ENTIDAD QUE REPRESENTA LOS ROLES DEL SISTEMA.
 * 
 * EJEMPLOS:
 * CLIENTE
 * VETERINARIO
 * ADMINISTRADOR
 * =========================================================
 */

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "rol")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Rol {

    /*
     * =========================================================
     * IDENTIFICADOR ÚNICO DEL ROL
     * =========================================================
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long idRol;

    /*
     * =========================================================
     * NOMBRE DEL ROL
     * =========================================================
     */
    @Column(name = "nombre_rol", nullable = false, unique = true)
    private String nombreRol;

    /*
     * =========================================================
     * RELACIÓN UNO A MUCHOS CON USUARIO
     * =========================================================
     */
    @OneToMany(mappedBy = "rol")
    private List<Usuario> usuarios;

}
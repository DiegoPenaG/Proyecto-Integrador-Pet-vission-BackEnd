package com.example.demo.usuario.model;

/*
 * =========================================================
 * USUARIO.JAVA
 * ---------------------------------------------------------
 * ENTIDAD QUE REPRESENTA LOS USUARIOS DEL SISTEMA.
 * 
 * CADA USUARIO TIENE UN ROL ASIGNADO.
 * =========================================================
 */

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Usuario {

    /*
     * =========================================================
     * IDENTIFICADOR ÚNICO DEL USUARIO
     * =========================================================
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    /*
     * =========================================================
     * NOMBRE DEL USUARIO
     * =========================================================
     */
    @Column(name = "nombre", nullable = false)
    private String nombre;

    /*
     * =========================================================
     * APELLIDO DEL USUARIO
     * =========================================================
     */
    @Column(name = "apellido", nullable = false)
    private String apellido;

    /*
     * =========================================================
     * CORREO ÚNICO DEL USUARIO
     * =========================================================
     */
    @Column(name = "correo", nullable = false, unique = true)
    private String correo;

    /*
     * =========================================================
     * CONTRASEÑA DEL USUARIO
     * =========================================================
     */
    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    /*
     * =========================================================
     * TELÉFONO DEL USUARIO
     * =========================================================
     */
    @Column(name = "telefono")
    private String telefono;

    /*
     * =========================================================
     * FOREIGN KEY HACIA ROL
     * =========================================================
     */
    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

}
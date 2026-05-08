package com.example.demo.usuario.model;

/*
 * =========================================================
 * USUARIOVETERINARIO.JAVA
 * ---------------------------------------------------------
 * ENTIDAD QUE REPRESENTA LA TABLA "USUARIO_VETERINARIO".
 * 
 * ALMACENA INFORMACIÓN ADICIONAL Y ESPECÍFICA DE LOS
 * VETERINARIOS.
 * 
 * SE RELACIONA CON USUARIO A TRAVÉS DE UNA RELACIÓN
 * UNO A UNO.
 * =========================================================
 */

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario_veterinario")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UsuarioVeterinario {

    /*
     * =========================================================
     * IDENTIFICADOR ÚNICO DEL REGISTRO VETERINARIO
     * =========================================================
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_veterinario")
    private Long idVeterinario;

    /*
     * =========================================================
     * ESPECIALIDAD DEL VETERINARIO
     * =========================================================
     */
    @Column(name = "especialidad")
    private String especialidad;

    /*
     * =========================================================
     * NÚMERO DE LICENCIA DEL VETERINARIO
     * =========================================================
     */
    @Column(name = "numero_licencia", unique = true)
    private String numeroLicencia;

    /*
     * =========================================================
     * RELACIÓN UNO A UNO CON USUARIO
     * =========================================================
     */
    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

}
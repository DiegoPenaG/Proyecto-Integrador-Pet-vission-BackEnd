// src/main/java/com/petvission/rol/model/Rol.java

package com.petvission.usuario.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rol")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long idRol;

    @Enumerated(EnumType.STRING)
    @Column(name = "nombre_rol", nullable = false, unique = true)
    private NombreRol nombreRol;

    // Enum con los valores fijos del sistema
    public enum NombreRol {
        CLIENTE,
        VETERINARIO,
        ADMINISTRADOR
    }

    // ❌ Se eliminó @OneToMany con Usuario
    // Esa relación no se necesita aquí
}
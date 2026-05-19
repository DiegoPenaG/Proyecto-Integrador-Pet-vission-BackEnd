// src/main/java/com/petvission/usuario/model/UsuarioVeterinario.java

package com.petvission.usuario.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario_veterinario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioVeterinario {

    // Comparte el mismo ID que Usuario
    // No tiene ID propio
    @Id
    private Long idUsuario;

    private String numColegiado;
    private String especialidad;

    @Column(columnDefinition = "TEXT")
    private String biografia;

    private String fotoPerfil;

    // @MapsId hace que idUsuario tome el valor
    // del id de Usuario automáticamente
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
}
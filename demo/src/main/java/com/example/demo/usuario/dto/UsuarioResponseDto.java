package com.example.demo.usuario.dto;

/*
 * =========================================================
 * DTO DE RESPUESTA PARA USUARIO
 * =========================================================
 */

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UsuarioResponseDto {

    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;
    private String rol;

}
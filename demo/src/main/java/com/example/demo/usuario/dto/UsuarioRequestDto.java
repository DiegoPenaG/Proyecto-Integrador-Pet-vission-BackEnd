package com.example.demo.usuario.dto;

/*
 * =========================================================
 * DTO DE ENTRADA PARA CREAR O ACTUALIZAR USUARIOS
 * =========================================================
 */

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UsuarioRequestDto {

    private String nombre;
    private String apellido;
    private String correo;
    private String contrasena;
    private String telefono;
    private Long idRol;

}
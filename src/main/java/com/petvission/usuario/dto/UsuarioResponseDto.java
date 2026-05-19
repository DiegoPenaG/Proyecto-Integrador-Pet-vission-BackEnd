package com.petvission.usuario.dto;

import lombok.Data;

@Data
public class UsuarioResponseDto {

    private Long idUsuario;
    private String nombres;
    private String apellidos;
    private String correo;
    private String telefono;
    private Boolean estado;
    private String rol;
    // ❌ contraseña eliminada — nunca se expone
}
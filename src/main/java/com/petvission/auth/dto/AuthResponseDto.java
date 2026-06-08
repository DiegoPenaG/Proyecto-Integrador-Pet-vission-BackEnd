// src/main/java/com/petvission/auth/dto/AuthResponseDto.java

package com.petvission.auth.dto;

import com.petvission.usuario.model.Rol.NombreRol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {

    private String token;
    private String tipo;
    private Long idUsuario;
    private String nombres;
    private String apellidos;
    private String correo;
    private NombreRol rol;

    // Solo cuando el admin tiene 2FA activo — el JWT no se emite hasta verificar el código
    private Boolean requiresTwoFactor;
}
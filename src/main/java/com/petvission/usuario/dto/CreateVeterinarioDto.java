package com.petvission.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateVeterinarioDto {

    @NotBlank
    private String nombres;

    @NotBlank
    private String apellidos;

    @Email
    @NotBlank
    private String correo;

    private String telefono;

    @NotBlank
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String contrasena;

    private String especialidad;
}

package org.example.petvission.mascota.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

/*
 * DTO DE SOLICITUD DE MASCOTA
 * UTILIZADO PARA RECIBIR LOS DATOS
 * NECESARIOS PARA EL REGISTRO
 * Y ACTUALIZACIÓN DE MASCOTAS.
 */

@Data
public class MascotaRequestDto {

    /*
     * NOMBRE DE LA MASCOTA
     */
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    /*
     * ESPECIE DE LA MASCOTA
     */
    @NotBlank(message = "La especie es obligatoria")
    private String especie;

    /*
     * INFORMACIÓN COMPLEMENTARIA
     * DE LA MASCOTA
     */
    private String raza;
    private String sexo;
    private LocalDate fechaNacimiento;
    private String color;
    private Double peso;
}
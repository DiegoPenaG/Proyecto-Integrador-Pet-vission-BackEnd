package com.petvission.mascota.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/*
 * DTO DE RESPUESTA DE MASCOTA
 * UTILIZADO PARA ENVIAR LA INFORMACIÓN
 * DE LAS MASCOTAS HACIA EL FRONTEND
 * O CLIENTE QUE CONSUMA LA API.
 */

@Data
public class MascotaResponseDto {

    /*
     * IDENTIFICADOR DE LA MASCOTA
     */
    private Long idMascota;

    /*
     * INFORMACIÓN PRINCIPAL
     * DE LA MASCOTA
     */
    private String nombre;
    private String especie;
    private String raza;
    private String sexo;
    private LocalDate fechaNacimiento;
    private String color;
    private BigDecimal peso;

    /*
     * ESTADO LÓGICO DE LA MASCOTA
     */
    private Boolean estado;
    private Boolean animalGuia;

    /*
     * INFORMACIÓN DEL USUARIO
     * PROPIETARIO DE LA MASCOTA
     */
    private Long idUsuario;
    private String nombreUsuario;
}
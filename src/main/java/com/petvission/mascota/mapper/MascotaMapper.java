package com.petvission.mascota.mapper;

import com.petvission.mascota.dto.MascotaRequestDto;
import com.petvission.mascota.dto.MascotaResponseDto;
import com.petvission.mascota.model.Mascota;
import org.springframework.stereotype.Component;


/*
 * MAPPER DE MASCOTAS
 * PERMITE CONVERTIR OBJETOS
 * ENTRE ENTITY Y DTO.
 */

@Component
public class MascotaMapper {

    /*
     * MÉTODO PARA CONVERTIR
     * ENTITY -> RESPONSE DTO
     */
    public MascotaResponseDto toDto(
            Mascota mascota) {

        MascotaResponseDto dto =
                new MascotaResponseDto();

        /*
         * ASIGNACIÓN DE DATOS
         * DE LA MASCOTA
         */
        dto.setIdMascota(
                mascota.getIdMascota()
        );
        dto.setNombre(
                mascota.getNombre()
        );
        dto.setEspecie(
                mascota.getEspecie()
        );

        dto.setRaza(
                mascota.getRaza()
        );

        dto.setSexo(
                mascota.getSexo()
        );

        dto.setFechaNacimiento(
                mascota.getFechaNacimiento()
        );

        dto.setColor(
                mascota.getColor()
        );

        dto.setPeso(mascota.getPeso());

        dto.setEstado(
                mascota.getEstado()
        );

        /*
         * ASIGNACIÓN DE DATOS
         * DEL USUARIO
         */
        dto.setIdUsuario(
                mascota.getUsuario().getIdUsuario()
        );

        dto.setNombreUsuario(
                mascota.getUsuario().getNombres()
                        + " " +
                        mascota.getUsuario().getApellidos()
        );

        return dto;
    }

    /*
     * MÉTODO PARA CONVERTIR
     * REQUEST DTO -> ENTITY
     */
    public Mascota toEntity(
            MascotaRequestDto dto) {

        Mascota mascota =
                new Mascota();

        /*
         * ASIGNACIÓN DE DATOS
         * DE LA MASCOTA
         */
        mascota.setNombre(
                dto.getNombre()
        );

        mascota.setEspecie(
                dto.getEspecie()
        );

        mascota.setRaza(
                dto.getRaza()
        );

        mascota.setSexo(
                dto.getSexo()
        );

        mascota.setFechaNacimiento(
                dto.getFechaNacimiento()
        );

        mascota.setColor(
                dto.getColor()
        );

        mascota.setPeso(
                dto.getPeso()
        );

        return mascota;
    }
}
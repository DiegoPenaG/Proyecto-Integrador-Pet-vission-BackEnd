package com.petvission.turno.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class SlotVetDisponibleDto {

    private Long idTurnoDetalle;
    private LocalTime horaInicio;
    private VetBriefDto veterinario;

    @Data
    @Builder
    public static class VetBriefDto {
        private Long idUsuario;
        private String nombres;
        private String apellidos;
    }
}

package com.petvission.mascota.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReasignarMascotaDto {

    @NotNull
    private Long idNuevoUsuario;
}

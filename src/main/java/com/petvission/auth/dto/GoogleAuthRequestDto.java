package com.petvission.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleAuthRequestDto {

    @NotBlank(message = "El token de Google es obligatorio")
    private String token;
}

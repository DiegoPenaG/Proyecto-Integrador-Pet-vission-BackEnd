package com.petvission.auth.dto;

import lombok.Data;

@Data
public class TwoFactorVerifyDto {
    private Long idUsuario;
    private String codigo;
}

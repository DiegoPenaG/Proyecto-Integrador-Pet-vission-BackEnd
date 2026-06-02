package com.petvission.historialClinico.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialClinicoResponseDto {

    private Long idHistorial;

    private Long idReserva;

    private String nombreMascota;

    private String nombreVeterinario;

    private Long idVeterinario;

    private String diagnostico;

    private String tratamiento;

    private String observaciones;

    private BigDecimal peso;

    private BigDecimal temperatura;

    private Integer frecuenciaCardiaca;

    private Integer frecuenciaRespiratoria;

    private Integer saturacionOxigeno;

    private LocalDateTime fechaRegistro;

    private String receta;

    private List<TratamientoResponseDto> tratamientos;

    private List<VacunaEnHistorialDto> vacunas;
}

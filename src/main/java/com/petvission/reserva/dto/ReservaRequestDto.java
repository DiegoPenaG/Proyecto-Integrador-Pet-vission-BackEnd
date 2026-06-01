package com.petvission.reserva.dto;

import com.petvission.reserva.model.CategoriaReserva;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRequestDto {

    @NotNull(message = "El id del usuario es obligatorio")
    private Long idUsuario;

    @NotNull(message = "El id del veterinario es obligatorio")
    private Long idVeterinario;

    /* Nullable — CONSULTA no selecciona un servicio específico. */
    private Integer idServicio;

    @NotNull(message = "La mascota es obligatoria")
    private Long idMascota;

    @NotNull(message = "El turno detalle es obligatorio")
    private Long idTurnoDetalle;

    @NotNull(message = "La categoría de la reserva es obligatoria")
    private CategoriaReserva categoriaReserva;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "La hora es obligatoria")
    private LocalTime hora;

    /* Solo para CONSULTA — descripción libre del motivo. */
    private String motivo;

    private String observaciones;
}

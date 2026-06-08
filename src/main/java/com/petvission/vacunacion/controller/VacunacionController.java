package com.petvission.vacunacion.controller;

import com.petvission.shared.response.ApiResponse;
import com.petvission.vacunacion.dto.VacunacionRequestDto;
import com.petvission.vacunacion.dto.VacunacionDesdeConsultaDto;
import com.petvission.vacunacion.dto.VacunacionResponseDto;
import com.petvission.vacunacion.model.VacunaCatalogo;
import com.petvission.vacunacion.service.VacunacionService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vacunacion")
@RequiredArgsConstructor
public class VacunacionController {

    private final VacunacionService vacunacionService;

    /*
     * REGISTRAR VACUNACIÓN
     */
    @PostMapping
    public ResponseEntity<ApiResponse<VacunacionResponseDto>> registrarVacunacion(
            @RequestBody VacunacionRequestDto dto
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(vacunacionService.registrarVacunacion(dto))
        );
    }

    /*
     * VACUNACIONES DE UNA MASCOTA — accesible por cliente y veterinario
     */
    @GetMapping("/mascota/{idMascota}")
    public ResponseEntity<ApiResponse<List<VacunacionResponseDto>>> getByMascota(
            @PathVariable Long idMascota
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(vacunacionService.getVacunasByMascota(idMascota))
        );
    }

    /*
     * CATÁLOGO DE VACUNAS — ?especie=CANINA|FELINA filtra y añade las generales; sin param devuelve todo
     */
    @GetMapping("/catalogo")
    public ResponseEntity<ApiResponse<List<VacunaCatalogo>>> obtenerCatalogo(
            @RequestParam(required = false) String especie
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(vacunacionService.obtenerCatalogo(especie))
        );
    }

    /*
     * REGISTRAR VACUNACIÓN DESDE CONSULTA (vet extraído del JWT)
     */
    @PreAuthorize("hasRole('VETERINARIO')")
    @PostMapping("/desde-consulta")
    public ResponseEntity<ApiResponse<VacunacionResponseDto>> registrarDesdeConsulta(
            @RequestBody VacunacionDesdeConsultaDto dto,
            Authentication auth
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                        vacunacionService.registrarDesdeConsulta(dto, auth)
                )
        );
    }
}
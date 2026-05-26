package com.petvission.vacunacion.controller;

import com.petvission.vacunacion.dto.VacunacionRequestDto;
import com.petvission.vacunacion.service.VacunacionService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vacunacion")
@RequiredArgsConstructor
public class VacunacionController {

    private final VacunacionService vacunacionService;

    /*
     * REGISTRAR VACUNACIÓN
     */
    @PostMapping
    public ResponseEntity<?> registrarVacunacion(
            @RequestBody VacunacionRequestDto dto
    ) {

        return ResponseEntity.ok(
                vacunacionService.registrarVacunacion(dto)
        );
    }
}
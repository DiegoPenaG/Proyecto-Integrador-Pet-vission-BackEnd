package org.example.petvission.cita.controller;

import lombok.RequiredArgsConstructor;
import org.example.petvission.cita.dto.ReprogramarCitaDto;
import org.example.petvission.cita.model.Cita;
import org.example.petvission.cita.service.CitaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaService citaService;

    /*
     * AGENDA GENERAL
     */
    @GetMapping("/agenda")
    public ResponseEntity<?> obtenerAgendaVeterinarios() {

        return ResponseEntity.ok(
                citaService.obtenerAgendaVeterinarios()
        );
    }

    /*
     * AGENDA MENSUAL VETERINARIO
     */
    @GetMapping("/agenda/veterinario/{idVeterinario}")
    public ResponseEntity<?> obtenerAgendaVeterinario(
            @PathVariable Long idVeterinario
    ) {

        return ResponseEntity.ok(
                citaService.obtenerAgendaMensualVeterinario(
                        idVeterinario
                )
        );
    }

    /*
     * CITAS POR USUARIO
     */
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<?> obtenerCitasUsuario(
            @PathVariable Long idUsuario
    ) {

        return ResponseEntity.ok(
                citaService.obtenerCitasPorUsuario(
                        idUsuario
                )
        );
    }

    /*
     * CITAS DEL VETERINARIO
     */
    @GetMapping("/veterinario/{idVeterinario}")
    public ResponseEntity<?> obtenerCitasVeterinario(
            @PathVariable Long idVeterinario
    ) {

        return ResponseEntity.ok(
                citaService.obtenerCitasVeterinario(
                        idVeterinario
                )
        );
    }

    /*
     * CANCELAR CITA
     */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Cita> cancelarCita(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                citaService.cancelarCita(id)
        );
    }

    /*
     * REPROGRAMAR CITA
     */
    @PatchMapping("/{id}/reprogramar")
    public ResponseEntity<Cita> reprogramarCita(
            @PathVariable Long id,
            @RequestBody ReprogramarCitaDto dto
    ) {

        return ResponseEntity.ok(
                citaService.reprogramarCita(id, dto)
        );
    }
}
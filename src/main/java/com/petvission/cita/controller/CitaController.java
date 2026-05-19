package com.petvission.cita.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.petvission.cita.dto.ReprogramarCitaDto;
import com.petvission.cita.model.Cita;
import com.petvission.cita.service.CitaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.petvission.cita.dto.CitaRequestDto;
import com.petvission.cita.dto.CitaResponseDto;
import com.petvission.usuario.model.Usuario;
import com.petvission.usuario.repository.UsuarioRepository;
import com.petvission.cita.mapper.CitaMapper;

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
     * DISPONIBILIDAD BÁSICA
     */
    @GetMapping("/disponibilidad")
    public ResponseEntity<?> obtenerDisponibilidadBasica() {

        return ResponseEntity.ok(
                citaService.obtenerDisponibilidadBasica()
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
     * CITAS POR FECHA
     */
    @GetMapping("/fecha")
    public ResponseEntity<?> obtenerCitasPorFecha(
            @RequestParam String fecha
    ) {

        return ResponseEntity.ok(
                citaService.obtenerCitasPorFecha(
                        java.time.LocalDate.parse(fecha)
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
    /*
     * AGENDAR CITA
     */
    @PostMapping
    public ResponseEntity<CitaResponseDto> agendarCita(
            @Valid @RequestBody CitaRequestDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(citaService.agendarCitaDto(dto));
    }
}
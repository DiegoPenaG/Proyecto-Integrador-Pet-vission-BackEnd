package com.petvission.cita.controller;


import com.petvission.cita.dto.CitaUsuarioDto;
import com.petvission.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.petvission.cita.dto.ReprogramarCitaDto;
import com.petvission.cita.service.CitaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.petvission.cita.dto.CitaRequestDto;
import com.petvission.cita.dto.CitaResponseDto;
import com.petvission.cita.dto.AgendaVeterinarioDto;



import java.util.List;

@RestController
@RequestMapping("/api/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaService citaService;

    /*
     * AGENDA GENERAL
     */

    @GetMapping("/agenda")
    public ResponseEntity<ApiResponse<List<AgendaVeterinarioDto>>> obtenerAgendaVeterinarios() {
        return ResponseEntity.ok(
                ApiResponse.success(citaService.obtenerAgendaVeterinarios())
        );
    }

    /*
     * AGENDA MENSUAL VETERINARIO
     */

    @GetMapping("/agenda/veterinario/{idVeterinario}")
    public ResponseEntity<ApiResponse<List<CitaUsuarioDto>>> obtenerAgendaVeterinario(
            @PathVariable Long idVeterinario
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        citaService.obtenerAgendaMensualVeterinario(idVeterinario)
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
    public ResponseEntity<ApiResponse<List<CitaUsuarioDto>>> obtenerCitasVeterinario(
            @PathVariable Long idVeterinario
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(citaService.obtenerCitasVeterinario(idVeterinario))
        );
    }

    /*
        * CITAS POR FECHA
     */
    @GetMapping("/fecha")
    public ResponseEntity<ApiResponse<List<CitaUsuarioDto>>> obtenerCitasPorFecha(
            @RequestParam String fecha
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        citaService.obtenerCitasPorFecha(
                                java.time.LocalDate.parse(fecha)
                        )
                )
        );
    }


    /*
        * CANCELAR CITA
     */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ApiResponse<CitaUsuarioDto>> cancelarCita(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(citaService.cancelarCita(id)));
    }

    /*
        * REPROGRAMAR CITA
     */

    @PatchMapping("/{id}/reprogramar")
    public ResponseEntity<ApiResponse<CitaUsuarioDto>> reprogramarCita(
            @PathVariable Long id,
            @RequestBody ReprogramarCitaDto dto
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(citaService.reprogramarCita(id, dto))
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
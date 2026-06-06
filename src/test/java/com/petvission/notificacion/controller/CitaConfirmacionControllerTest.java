package com.petvission.notificacion.controller;

import com.petvission.notificacion.service.CitaConfirmacionService;
import com.petvission.reserva.model.EstadoReserva;
import com.petvission.shared.exception.GlobalExceptionHandler;
import com.petvission.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CitaConfirmacionControllerTest {

    MockMvc mockMvc;

    @Mock
    CitaConfirmacionService citaConfirmacionService;

    @InjectMocks
    CitaConfirmacionController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ─── POST /{id}/confirmar ─────────────────────────────────────────

    @Test
    void confirmar_citaValida_retorna200() throws Exception {
        doNothing().when(citaConfirmacionService).confirmarCita(1L, "token-ok");

        mockMvc.perform(post("/api/reservas/1/confirmar")
                        .param("token", "token-ok"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("¡Cita confirmada! Nos vemos pronto."));
    }

    @Test
    void confirmar_yaConfirmada_retorna409() throws Exception {
        doThrow(new IllegalStateException("La cita ya fue confirmada previamente"))
                .when(citaConfirmacionService).confirmarCita(2L, "token-ok");

        mockMvc.perform(post("/api/reservas/2/confirmar")
                        .param("token", "token-ok"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void confirmar_fechaPasada_retorna409() throws Exception {
        doThrow(new IllegalStateException("No se puede confirmar: la fecha de la cita ya pasó"))
                .when(citaConfirmacionService).confirmarCita(3L, "token-ok");

        mockMvc.perform(post("/api/reservas/3/confirmar")
                        .param("token", "token-ok"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("No se puede confirmar: la fecha de la cita ya pasó"));
    }

    @Test
    void confirmar_tokenInvalido_retorna404() throws Exception {
        // Token que no pertenece a esta reserva → simula "no es el dueño"
        doThrow(new ResourceNotFoundException("Enlace de confirmación inválido o no pertenece a esta cita"))
                .when(citaConfirmacionService).confirmarCita(4L, "token-ajeno");

        mockMvc.perform(post("/api/reservas/4/confirmar")
                        .param("token", "token-ajeno"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ─── GET /{id}/estado ─────────────────────────────────────────────

    @Test
    void obtenerEstado_reservaExistente_retornaEstado() throws Exception {
        when(citaConfirmacionService.obtenerEstadoCita(5L)).thenReturn(EstadoReserva.CONFIRMADA);

        mockMvc.perform(get("/api/reservas/5/estado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("CONFIRMADA"));
    }

    @Test
    void obtenerEstado_reservaNoExistente_retorna404() throws Exception {
        when(citaConfirmacionService.obtenerEstadoCita(99L))
                .thenThrow(new ResourceNotFoundException("Reserva no encontrada"));

        mockMvc.perform(get("/api/reservas/99/estado"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
}

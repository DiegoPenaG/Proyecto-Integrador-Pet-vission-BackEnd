package com.petvission.horario.controller;

import lombok.RequiredArgsConstructor;
import com.petvission.horario.repository.Horario;
import com.petvission.horario.service.HorarioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/horarios")
@CrossOrigin(origins = "*")

@RequiredArgsConstructor

public class HorarioController {

    private final HorarioService horarioService;

    @GetMapping
    public List<Horario> listarHorarios() {
        return horarioService.obtenerHorarios();
    }

    @PostMapping
    public Horario crearHorario(@RequestBody Horario horario) {
        return horarioService.guardarHorario(horario);
    }
}

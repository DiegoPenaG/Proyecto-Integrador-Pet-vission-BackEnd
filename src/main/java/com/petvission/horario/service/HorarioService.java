package com.petvission.horario.service;

import lombok.RequiredArgsConstructor;
import com.petvission.horario.model.Horario;
import com.petvission.horario.repository.HorarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HorarioService {

    private final HorarioRepository horarioRepository;

    public List<Horario> obtenerHorarios() {
        return horarioRepository.findAll();
    }

    public Horario guardarHorario(Horario horario) {
        return horarioRepository.save(horario);
    }
}
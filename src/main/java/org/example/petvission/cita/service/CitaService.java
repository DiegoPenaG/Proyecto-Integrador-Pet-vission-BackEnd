package org.example.petvission.cita.service;

import org.example.petvission.cita.model.Cita;
import org.example.petvission.cita.model.EstadoCita;
import org.example.petvission.cita.repository.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CitaService {

    @Autowired
    private CitaRepository citaRepository;

    // Tu objetivo: AGENDAR CITA
    public Cita agendarCita(Cita cita) {
        if (citaRepository.existsByVeterinarioAndFechaAndHora(
                cita.getVeterinario(), cita.getFecha(), cita.getHora())) {
            throw new RuntimeException("El veterinario ya tiene una cita en ese horario.");
        }
        cita.setEstado(EstadoCita.PENDIENTE);
        return citaRepository.save(cita);
    }
}
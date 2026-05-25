package com.petvission.servicio.service;

import com.petvission.servicio.dto.ServicioRequestDto;
import com.petvission.servicio.dto.ServicioResponseDto;
import com.petvission.servicio.mapper.ServicioMapper;
import com.petvission.servicio.model.Servicio;
import com.petvission.servicio.repository.ServicioRepository;
import com.petvission.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioService {

    private final ServicioRepository servicioRepository;

    private final ServicioMapper servicioMapper;

    /*
     * CREAR SERVICIO
     */
    public ServicioResponseDto crearServicio(
            ServicioRequestDto dto
    ) {
        Servicio servicio = servicioMapper.toEntity(dto);
        Servicio saved = servicioRepository.save(servicio);
        return servicioMapper.toDto(saved);
    }

    /*
     * OBTENER TODOS LOS SERVICIOS
     */
    public List<ServicioResponseDto> obtenerTodos() {
        return servicioRepository.findAll()
                .stream()
                .map(servicioMapper::toDto)
                .toList();
    }

    /*
     * OBTENER SERVICIOS ACTIVOS
     */
    public List<ServicioResponseDto> obtenerActivos() {
        return servicioRepository.findByActivoTrue()
                .stream()
                .map(servicioMapper::toDto)
                .toList();
    }

    /*
     * OBTENER SERVICIOS POR CATEGORÍA
     */
    public List<ServicioResponseDto> obtenerPorCategoria(
            String categoria
    ) {
        return servicioRepository.findByCategoria(categoria)
                .stream()
                .map(servicioMapper::toDto)
                .toList();
    }

    /*
     * OBTENER SERVICIO POR ID
     */
    public ServicioResponseDto obtenerPorId(Integer id) {
        Servicio servicio = servicioRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Servicio no encontrado"
                        )
                );
        return servicioMapper.toDto(servicio);
    }

    /*
     * ACTUALIZAR SERVICIO
     */
    public ServicioResponseDto actualizarServicio(
            Integer id,
            ServicioRequestDto dto
    ) {
        Servicio servicio = servicioRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Servicio no encontrado"
                        )
                );

        servicio.setNombre(dto.getNombre());
        servicio.setCategoria(dto.getCategoria());
        servicio.setDescripcion(dto.getDescripcion());
        servicio.setDuracionMinutos(dto.getDuracionMinutos());
        servicio.setPrecio(dto.getPrecio());
        servicio.setActivo(dto.getActivo());

        Servicio saved = servicioRepository.save(servicio);
        return servicioMapper.toDto(saved);
    }

    /*
     * DESACTIVAR SERVICIO (baja lógica)
     */
    public ServicioResponseDto desactivarServicio(Integer id) {
        Servicio servicio = servicioRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Servicio no encontrado"
                        )
                );

        servicio.setActivo(false);
        Servicio saved = servicioRepository.save(servicio);
        return servicioMapper.toDto(saved);
    }
}

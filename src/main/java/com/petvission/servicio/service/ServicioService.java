package com.petvission.servicio.service;

import com.petvission.servicio.dto.ServicioRequestDto;
import com.petvission.servicio.dto.ServicioResponseDto;
import com.petvission.servicio.mapper.ServicioMapper;
import com.petvission.servicio.model.Servicio;
import com.petvission.servicio.model.TipoServicio;
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
    public ServicioResponseDto crearServicio(ServicioRequestDto dto) {
        return servicioMapper.toDto(servicioRepository.save(servicioMapper.toEntity(dto)));
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
     * Si se indica tipo (VACUNACION | LABORATORIO) filtra por ese valor.
     * Sin tipo retorna todos los activos.
     */
    public List<ServicioResponseDto> obtenerActivos(TipoServicio tipo) {
        List<Servicio> servicios = (tipo != null)
                ? servicioRepository.findByActivoTrueAndTipoServicio(tipo)
                : servicioRepository.findByActivoTrue();
        return servicios.stream().map(servicioMapper::toDto).toList();
    }

    /*
     * OBTENER SERVICIO POR ID
     */
    public ServicioResponseDto obtenerPorId(Integer id) {
        return servicioMapper.toDto(
                servicioRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"))
        );
    }

    /*
     * ACTUALIZAR SERVICIO
     */
    public ServicioResponseDto actualizarServicio(Integer id, ServicioRequestDto dto) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

        servicio.setNombre(dto.getNombre());
        servicio.setTipoServicio(dto.getTipoServicio());
        servicio.setDescripcion(dto.getDescripcion());
        servicio.setDuracionMinutos(dto.getDuracionMinutos());
        servicio.setPrecio(dto.getPrecio());
        servicio.setActivo(dto.getActivo());

        return servicioMapper.toDto(servicioRepository.save(servicio));
    }

    /*
     * DESACTIVAR SERVICIO (baja lógica)
     */
    public ServicioResponseDto desactivarServicio(Integer id) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));
        servicio.setActivo(false);
        return servicioMapper.toDto(servicioRepository.save(servicio));
    }
}

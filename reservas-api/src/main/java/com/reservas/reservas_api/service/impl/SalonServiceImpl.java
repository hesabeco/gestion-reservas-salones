package com.reservas.reservas_api.service.impl;

import com.reservas.reservas_api.dto.request.SalonRequest;
import com.reservas.reservas_api.dto.response.SalonResponse;
import com.reservas.reservas_api.dto.response.SucursalResponse;
import com.reservas.reservas_api.dto.response.UsuarioResponse;
import com.reservas.reservas_api.entity.Rol;
import com.reservas.reservas_api.entity.Salon;
import com.reservas.reservas_api.entity.Sucursal;
import com.reservas.reservas_api.entity.Usuario;
import com.reservas.reservas_api.exception.BusinessException;
import com.reservas.reservas_api.exception.ResourceNotFoundException;
import com.reservas.reservas_api.repository.SalonRepository;
import com.reservas.reservas_api.repository.SucursalRepository;
import com.reservas.reservas_api.repository.UsuarioRepository;
import com.reservas.reservas_api.service.SalonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalonServiceImpl implements SalonService {

    private final SalonRepository salonRepository;
    private final SucursalRepository sucursalRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public SalonResponse crear(SalonRequest request) {
        if (salonRepository.existsByNombreAndSucursalId(request.getNombre(), request.getSucursalId())) {
            throw new BusinessException("Ya existe un salón con ese nombre en esta sucursal");
        }
        Sucursal sucursal = obtenerSucursal(request.getSucursalId());
        Usuario gestor = obtenerGestor(request.getGestorId());

        Salon salon = Salon.builder()
                .nombre(request.getNombre())
                .capacidadMaxima(request.getCapacidadMaxima())
                .costoPorHora(request.getCostoPorHora())
                .sucursal(sucursal)
                .gestor(gestor)
                .build();

        salonRepository.save(salon);
        log.info("Salón creado: {}", salon.getNombre());
        return mapToResponse(salon);
    }

    @Override
    public SalonResponse actualizar(Long id, SalonRequest request) {
        if (salonRepository.existsByNombreAndSucursalIdAndIdNot(request.getNombre(), request.getSucursalId(), id)) {
            throw new BusinessException("Ya existe un salón con ese nombre en esta sucursal");
        }
        Salon salon = obtenerSalon(id);
        Sucursal sucursal = obtenerSucursal(request.getSucursalId());
        Usuario gestor = obtenerGestor(request.getGestorId());

        salon.setNombre(request.getNombre());
        salon.setCapacidadMaxima(request.getCapacidadMaxima());
        salon.setCostoPorHora(request.getCostoPorHora());
        salon.setSucursal(sucursal);
        salon.setGestor(gestor);

        salonRepository.save(salon);
        log.info("Salón actualizado: {}", salon.getNombre());
        return mapToResponse(salon);
    }

    @Override
    public SalonResponse obtenerPorId(Long id) {
        return mapToResponse(obtenerSalon(id));
    }

    @Override
    public List<SalonResponse> obtenerTodos() {
        return salonRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<SalonResponse> obtenerPorGestor(Long gestorId) {
        return salonRepository.findByGestorId(gestorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void eliminar(Long id) {
        obtenerSalon(id);
        salonRepository.deleteById(id);
        log.info("Salón eliminado: {}", id);
    }

    private Sucursal obtenerSucursal(Long id) {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));
    }

    private Usuario obtenerGestor(Long gestorId) {
        Usuario gestor = usuarioRepository.findById(gestorId)
                .orElseThrow(() -> new ResourceNotFoundException("Gestor no encontrado"));
        if (!gestor.getRol().equals(Rol.GESTOR)) {
            throw new BusinessException("El usuario indicado no tiene rol GESTOR");
        }
        return gestor;
    }

    private Salon obtenerSalon(Long id) {
        return salonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salón no encontrado"));
    }

    @Override
    public SalonResponse desactivar(Long id) {
        Salon salon = obtenerSalon(id);
        salon.setActivo(false);
        salonRepository.save(salon);
        log.info("Salón desactivado: {}", id);
        return mapToResponse(salon);
    }

    public SalonResponse mapToResponse(Salon salon) {
        return SalonResponse.builder()
                .id(salon.getId())
                .nombre(salon.getNombre())
                .capacidadMaxima(salon.getCapacidadMaxima())
                .costoPorHora(salon.getCostoPorHora())
                .sucursal(SucursalResponse.builder()
                        .id(salon.getSucursal().getId())
                        .nombre(salon.getSucursal().getNombre())
                        .direccion(salon.getSucursal().getDireccion())
                        .gestor(UsuarioResponse.builder()
                                .id(salon.getSucursal().getGestor().getId())
                                .nombre(salon.getSucursal().getGestor().getNombre())
                                .email(salon.getSucursal().getGestor().getEmail())
                                .rol(salon.getSucursal().getGestor().getRol().name())
                                .build())
                        .build())
                .gestor(UsuarioResponse.builder()
                        .id(salon.getGestor().getId())
                        .nombre(salon.getGestor().getNombre())
                        .email(salon.getGestor().getEmail())
                        .rol(salon.getGestor().getRol().name())
                        .build())
                .build();
    }
}
package com.reservas.reservas_api.service.impl;

import com.reservas.reservas_api.dto.request.SucursalRequest;
import com.reservas.reservas_api.dto.response.SucursalResponse;
import com.reservas.reservas_api.dto.response.UsuarioResponse;
import com.reservas.reservas_api.entity.Rol;
import com.reservas.reservas_api.entity.Sucursal;
import com.reservas.reservas_api.entity.Usuario;
import com.reservas.reservas_api.exception.BusinessException;
import com.reservas.reservas_api.exception.ResourceNotFoundException;
import com.reservas.reservas_api.repository.SucursalRepository;
import com.reservas.reservas_api.repository.SalonRepository;
import com.reservas.reservas_api.repository.UsuarioRepository;
import com.reservas.reservas_api.service.SucursalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import com.reservas.reservas_api.util.SecurityUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class SucursalServiceImpl implements SucursalService {

    private final SucursalRepository sucursalRepository;
    private final SalonRepository salonRepository;
    private final UsuarioRepository usuarioRepository;
    private final SecurityUtils securityUtils;

    @Override
    public SucursalResponse crear(SucursalRequest request) {
        if (sucursalRepository.existsByNombre(request.getNombre())) {
            throw new BusinessException("Ya existe una sucursal con ese nombre");
        }
        Usuario gestor = obtenerGestor(request.getGestorId());
        Sucursal sucursal = Sucursal.builder()
                .nombre(request.getNombre())
                .direccion(request.getDireccion())
                .gestor(gestor)
                .build();
        sucursalRepository.save(sucursal);
        log.info("Sucursal creada: {}", sucursal.getNombre());
        return mapToResponse(sucursal);
    }

    @Override
    public SucursalResponse actualizar(Long id, SucursalRequest request) {
        if (sucursalRepository.existsByNombreAndIdNot(request.getNombre(), id)) {
            throw new BusinessException("Ya existe una sucursal con ese nombre");
        }
        Sucursal sucursal = obtenerSucursal(id);
        Usuario gestor = obtenerGestor(request.getGestorId());
        sucursal.setNombre(request.getNombre());
        sucursal.setDireccion(request.getDireccion());
        sucursal.setGestor(gestor);
        sucursalRepository.save(sucursal);
        log.info("Sucursal actualizada: {}", sucursal.getNombre());
        return mapToResponse(sucursal);
    }

    @Override
    public SucursalResponse obtenerPorId(Long id) {
        return mapToResponse(obtenerSucursal(id));
    }

    @Override
    public List<SucursalResponse> obtenerTodas() {
        return sucursalRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<SucursalResponse> obtenerPorGestor(Long gestorId) {
        return sucursalRepository.findByGestorId(gestorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void eliminar(Long id) {
        obtenerSucursal(id);
        sucursalRepository.deleteById(id);
        log.info("Sucursal eliminada: {}", id);
    }

    private Usuario obtenerGestor(Long gestorId) {
        Usuario gestor = usuarioRepository.findById(gestorId)
                .orElseThrow(() -> new ResourceNotFoundException("Gestor no encontrado"));
        if (!gestor.getRol().equals(Rol.GESTOR)) {
            throw new BusinessException("El usuario indicado no tiene rol GESTOR");
        }
        return gestor;
    }

    private Sucursal obtenerSucursal(Long id) {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));
    }

    @Override
    public SucursalResponse desactivar(Long id) {
        Sucursal sucursal = obtenerSucursal(id);

        boolean tieneSalones = salonRepository.existsBySucursalIdAndActivoTrue(id);
        log.info("Tiene salones activos: {}", tieneSalones);

        if (tieneSalones) {
            throw new BusinessException(
                    "No se puede desactivar la sucursal, tiene salones activos asociados");
        }

        sucursal.setActivo(false);
        sucursalRepository.save(sucursal);
        log.info("Sucursal desactivada: {}", id);
        return mapToResponse(sucursal);
    }

    public SucursalResponse mapToResponse(Sucursal sucursal) {
        return SucursalResponse.builder()
                .id(sucursal.getId())
                .nombre(sucursal.getNombre())
                .direccion(sucursal.getDireccion())
                .gestor(UsuarioResponse.builder()
                        .id(sucursal.getGestor().getId())
                        .nombre(sucursal.getGestor().getNombre())
                        .email(sucursal.getGestor().getEmail())
                        .rol(sucursal.getGestor().getRol().name())
                        .build())
                .build();
    }

    @Override
    public List<SucursalResponse> obtenerSegunUsuarioAutenticado() {
        Usuario usuario = securityUtils.obtenerUsuarioAutenticado();

        if (securityUtils.esAdmin(usuario)) {
            return sucursalRepository.findAll()
                    .stream()
                    .map(this::mapToResponse)
                    .toList();
        }

        return sucursalRepository.findByGestorId(usuario.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
}
package com.reservas.reservas_api.service;

import com.reservas.reservas_api.dto.request.SucursalRequest;
import com.reservas.reservas_api.dto.response.SucursalResponse;

import java.util.List;

public interface SucursalService {
    SucursalResponse crear(SucursalRequest request);
    SucursalResponse actualizar(Long id, SucursalRequest request);
    SucursalResponse obtenerPorId(Long id);
    List<SucursalResponse> obtenerTodas();
    List<SucursalResponse> obtenerPorGestor(Long gestorId);
    void eliminar(Long id);
}
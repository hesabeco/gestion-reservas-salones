package com.reservas.reservas_api.service;

import com.reservas.reservas_api.dto.request.SalonRequest;
import com.reservas.reservas_api.dto.response.SalonResponse;
import java.util.List;

public interface SalonService {
    SalonResponse crear(SalonRequest request);
    SalonResponse actualizar(Long id, SalonRequest request);
    SalonResponse obtenerPorId(Long id);
    List<SalonResponse> obtenerTodos();
    List<SalonResponse> obtenerPorGestor(Long gestorId);
    void eliminar(Long id);
    SalonResponse desactivar(Long id);
}

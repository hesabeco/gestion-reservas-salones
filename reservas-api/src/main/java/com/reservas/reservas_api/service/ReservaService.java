package com.reservas.reservas_api.service;

import com.reservas.reservas_api.dto.request.FinalizarReservaRequest;
import com.reservas.reservas_api.dto.request.RechazarReservaRequest;
import com.reservas.reservas_api.dto.request.ReservaRequest;
import com.reservas.reservas_api.dto.response.FinalizarReservaResponse;
import com.reservas.reservas_api.dto.response.ReservaResponse;
import java.util.List;
import java.util.Map;

public interface ReservaService {
    Map<String, Long> registrar(ReservaRequest request);
    FinalizarReservaResponse finalizar(FinalizarReservaRequest request);
    List<ReservaResponse> obtenerActivasPorSalon(Long salonId);
    List<ReservaResponse> buscarPorDocumento(String documento);
    void aprobar(Long id);
    void rechazar(Long id, RechazarReservaRequest request);
    void verificarExpiracion();
}
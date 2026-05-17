package com.reservas.reservas_api.service;

import com.reservas.reservas_api.dto.response.ClienteTopResponse;
import com.reservas.reservas_api.dto.response.GananciasResponse;
import com.reservas.reservas_api.dto.response.SucursalFacturacionResponse;
import java.util.List;

public interface IndicadorService {

    // Top 10 clientes con más reservas en todo el sistema (activas + histórico)
    List<ClienteTopResponse> top10ClientesGlobal();

    // Top 10 clientes con más reservas en un salón específico (activas + histórico)
    List<ClienteTopResponse> top10ClientesPorSalon(Long salonId);

    // Reservas activas de clientes que reservan por primera vez en ese salón
    List<ClienteTopResponse> primeraVezEnSalon(Long salonId);

    // Ganancias de un salón: hoy, esta semana, este mes, este año (GESTOR)
    GananciasResponse obtenerGanancias(Long salonId);

    // Top 3 sucursales con mayor facturación en el mes actual (ADMIN)
    List<SucursalFacturacionResponse> top3SucursalesMes();
}

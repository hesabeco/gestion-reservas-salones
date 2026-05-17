package com.reservas.reservas_api.controller;

import com.reservas.reservas_api.dto.response.ClienteTopResponse;
import com.reservas.reservas_api.dto.response.GananciasResponse;
import com.reservas.reservas_api.dto.response.SucursalFacturacionResponse;
import com.reservas.reservas_api.service.IndicadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/indicadores")
@RequiredArgsConstructor
public class IndicadorController {

    private final IndicadorService indicadorService;

    // Top 10 clientes con más reservas en todo el sistema - ADMIN y GESTOR
    @GetMapping("/top10-global")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR')")
    public ResponseEntity<List<ClienteTopResponse>> top10ClientesGlobal() {
        return ResponseEntity.ok(indicadorService.top10ClientesGlobal());
    }

    // Top 10 clientes con más reservas en un salón específico - ADMIN y GESTOR
    @GetMapping("/top10-salon/{salonId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR')")
    public ResponseEntity<List<ClienteTopResponse>> top10ClientesPorSalon(@PathVariable Long salonId) {
        return ResponseEntity.ok(indicadorService.top10ClientesPorSalon(salonId));
    }

    // Reservas activas de clientes que reservan por primera vez en ese salón - ADMIN y GESTOR
    @GetMapping("/primera-vez/{salonId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR')")
    public ResponseEntity<List<ClienteTopResponse>> primeraVezEnSalon(@PathVariable Long salonId) {
        return ResponseEntity.ok(indicadorService.primeraVezEnSalon(salonId));
    }

    // Ganancias de un salón por períodos - solo GESTOR
    @GetMapping("/ganancias/salon/{salonId}")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<GananciasResponse> obtenerGanancias(@PathVariable Long salonId) {
        return ResponseEntity.ok(indicadorService.obtenerGanancias(salonId));
    }

    // Top 3 sucursales con mayor facturación del mes actual - solo ADMIN
    @GetMapping("/top3-sucursales-mes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SucursalFacturacionResponse>> top3SucursalesMes() {
        return ResponseEntity.ok(indicadorService.top3SucursalesMes());
    }
}
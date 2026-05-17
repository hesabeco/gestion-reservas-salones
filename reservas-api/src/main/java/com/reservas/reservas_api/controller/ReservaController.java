package com.reservas.reservas_api.controller;

import com.reservas.reservas_api.dto.request.FinalizarReservaRequest;
import com.reservas.reservas_api.dto.request.ReservaRequest;
import com.reservas.reservas_api.dto.response.FinalizarReservaResponse;
import com.reservas.reservas_api.dto.response.ReservaResponse;
import com.reservas.reservas_api.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<Map<String, Long>> registrar(@Valid @RequestBody ReservaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.registrar(request));
    }

    @PostMapping("/finalizar")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<FinalizarReservaResponse> finalizar(@Valid @RequestBody FinalizarReservaRequest request) {
        return ResponseEntity.ok(reservaService.finalizar(request));
    }

    @GetMapping("/activas/salon/{salonId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR')")
    public ResponseEntity<List<ReservaResponse>> obtenerActivasPorSalon(@PathVariable Long salonId) {
        return ResponseEntity.ok(reservaService.obtenerActivasPorSalon(salonId));
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR')")
    public ResponseEntity<List<ReservaResponse>> buscarPorDocumento(@RequestParam String documento) {
        return ResponseEntity.ok(reservaService.buscarPorDocumento(documento));
    }
}
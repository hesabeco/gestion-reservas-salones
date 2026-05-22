package com.reservas.reservas_api.controller;

import com.reservas.reservas_api.dto.request.SucursalRequest;
import com.reservas.reservas_api.dto.response.SucursalResponse;
import com.reservas.reservas_api.service.SucursalService;
import com.reservas.reservas_api.dto.response.MensajeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

@RestController
@RequestMapping("/sucursales")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class SucursalController {

    private final SucursalService sucursalService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SucursalResponse> crear(@Valid @RequestBody SucursalRequest request) {
        return ResponseEntity.ok(sucursalService.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SucursalResponse> actualizar(@PathVariable Long id,
                                                       @Valid @RequestBody SucursalRequest request) {
        return ResponseEntity.ok(sucursalService.actualizar(id, request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR')")
    public ResponseEntity<SucursalResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(sucursalService.obtenerPorId(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR')")
    public ResponseEntity<List<SucursalResponse>> obtenerTodas() {
        return ResponseEntity.ok(sucursalService.obtenerSegunUsuarioAutenticado());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MensajeResponse> eliminar(@PathVariable Long id) {
        sucursalService.eliminar(id);
        return ResponseEntity.ok(MensajeResponse.builder()
                .mensaje("Sucursal eliminada exitosamente")
                .build());
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SucursalResponse> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(sucursalService.desactivar(id));
    }
}
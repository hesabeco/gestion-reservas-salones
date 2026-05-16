package com.reservas.reservas_api.controller;

import com.reservas.reservas_api.dto.request.SalonRequest;
import com.reservas.reservas_api.dto.response.SalonResponse;
import com.reservas.reservas_api.service.SalonService;
import com.reservas.reservas_api.dto.response.MensajeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salones")
@RequiredArgsConstructor
public class SalonController {

    private final SalonService salonService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SalonResponse> crear(@Valid @RequestBody SalonRequest request) {
        return ResponseEntity.ok(salonService.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SalonResponse> actualizar(@PathVariable Long id,
                                                    @Valid @RequestBody SalonRequest request) {
        return ResponseEntity.ok(salonService.actualizar(id, request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR')")
    public ResponseEntity<SalonResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(salonService.obtenerPorId(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR')")
    public ResponseEntity<List<SalonResponse>> obtenerTodos() {
        return ResponseEntity.ok(salonService.obtenerTodos());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MensajeResponse> eliminar(@PathVariable Long id) {
        salonService.eliminar(id);
        return ResponseEntity.ok(MensajeResponse.builder()
                .mensaje("Salon eliminado exitosamente")
                .build());
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SalonResponse> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(salonService.desactivar(id));
    }
}
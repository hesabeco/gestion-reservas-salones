package com.reservas.reservas_api.controller;

import com.reservas.reservas_api.dto.request.NotificacionRequest;
import com.reservas.reservas_api.dto.response.MensajeResponse;
import com.reservas.reservas_api.service.NotificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/notificaciones")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class NotificacionController {

    private final NotificacionService notificacionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MensajeResponse> enviar(@Valid @RequestBody NotificacionRequest request) {
        notificacionService.enviarNotificacion(
                request.getEmail(),
                request.getDocumento(),
                request.getMensaje(),
                request.getSalonId()
        );
        return ResponseEntity.ok(MensajeResponse.builder().mensaje("Notificación Enviada").build());
    }
}
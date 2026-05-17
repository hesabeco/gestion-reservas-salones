package com.notificaciones.notificaciones_api.controller;

import com.notificaciones.notificaciones_api.dto.request.NotificacionRequest;
import com.notificaciones.notificaciones_api.dto.response.NotificacionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {

    @PostMapping
    public ResponseEntity<NotificacionResponse> enviar(@RequestBody NotificacionRequest request) {
        log.info("Notificación recibida:");
        log.info("Email: {}", request.getEmail());
        log.info("Documento: {}", request.getDocumento());
        log.info("Mensaje: {}", request.getMensaje());
        log.info("Salón: {}", request.getSalonNombre());

        return ResponseEntity.ok(NotificacionResponse.builder()
                .mensaje("Notificación Enviada")
                .build());
    }
}
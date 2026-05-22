package com.reservas.reservas_api.service.impl;

import com.reservas.reservas_api.dto.response.MensajeResponse;
import com.reservas.reservas_api.entity.EstadoReserva;
import com.reservas.reservas_api.entity.Salon;
import com.reservas.reservas_api.exception.BusinessException;
import com.reservas.reservas_api.exception.ResourceNotFoundException;
import com.reservas.reservas_api.repository.ReservaRepository;
import com.reservas.reservas_api.repository.SalonRepository;
import com.reservas.reservas_api.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {

    private final ReservaRepository reservaRepository;
    private final SalonRepository salonRepository;
    private final RestTemplate restTemplate;

    @Value("${notification.service.url}")
    private String notificationServiceUrl;

    @Override
    public MensajeResponse enviarNotificacion(String email, String documento, String mensaje, String salonId) {
        Long salonIdLong = Long.parseLong(salonId);

        if (!reservaRepository.existsByDocumentoClienteAndSalonIdAndEstado(
                documento, salonIdLong, EstadoReserva.ACTIVA)) {
            throw new BusinessException(
                    "El cliente no tiene una reserva activa en el salón indicado");
        }

        Salon salon = salonRepository.findById(salonIdLong)
                .orElseThrow(() -> new ResourceNotFoundException("Salón no encontrado"));

        try {
            Map<String, String> body = new HashMap<>();
            body.put("email", email);
            body.put("documento", documento);
            body.put("mensaje", mensaje);
            body.put("salonNombre", salon.getNombre());

            MensajeResponse response = restTemplate.postForObject(
                    notificationServiceUrl + "/notificaciones",
                    body,
                    MensajeResponse.class
            );
            log.info("Notificación enviada: {}", response != null ? response.getMensaje() : "sin respuesta");
            return response;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al enviar notificación: {}", e.getMessage());
            throw new BusinessException("Error al conectar con el servicio de notificaciones");
        }
    }
}
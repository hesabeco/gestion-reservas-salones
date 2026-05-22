package com.reservas.reservas_api.service;
import com.reservas.reservas_api.dto.response.MensajeResponse;

public interface NotificacionService {
    MensajeResponse  enviarNotificacion(String email, String documento, String mensaje, String salonId);
}

package com.reservas.reservas_api.service;

public interface NotificacionService {
    void enviarNotificacion(String email, String documento, String mensaje, String salonId);
}

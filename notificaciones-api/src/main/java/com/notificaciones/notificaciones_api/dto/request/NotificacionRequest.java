package com.notificaciones.notificaciones_api.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificacionRequest {
    private String email;
    private String documento;
    private String mensaje;
    private String salonNombre;
}
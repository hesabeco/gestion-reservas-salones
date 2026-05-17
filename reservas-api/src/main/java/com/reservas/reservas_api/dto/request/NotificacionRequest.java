package com.reservas.reservas_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificacionRequest {

    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "El documento es obligatorio")
    private String documento;

    @NotBlank(message = "El mensaje es obligatorio")
    private String mensaje;

    @NotBlank(message = "El salonId es obligatorio")
    private String salonId;
}

package com.reservas.reservas_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RechazarReservaRequest {

    @NotBlank(message = "El motivo de rechazo es obligatorio")
    private String motivo;
}
package com.reservas.reservas_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinalizarReservaRequest {

    @NotBlank(message = "El documento del cliente es obligatorio")
    @Pattern(regexp = "^[0-9]{6,12}$", message = "El documento debe tener entre 6 y 12 caracteres numéricos")
    private String documentoCliente;

    @NotNull(message = "El id del salón es obligatorio")
    private Long salonId;
}
package com.reservas.reservas_api.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ReservaRequest {

    @NotBlank(message = "El documento del cliente es obligatorio")
    @Pattern(regexp = "^[0-9]{6,12}$", message = "El documento debe tener entre 6 y 12 caracteres numéricos")
    private String documentoCliente;

    @NotBlank(message = "El nombre del cliente es obligatorio")
    private String nombreCliente;

    @NotNull(message = "El id del salón es obligatorio")
    private Long salonId;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin estimada es obligatoria")
    private LocalDateTime fechaFinEstimada;

    @NotNull(message = "El número de asistentes es obligatorio")
    @Positive(message = "El número de asistentes debe ser mayor a cero")
    private Integer asistentes;
}

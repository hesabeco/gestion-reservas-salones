package com.reservas.reservas_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalonRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "La capacidad máxima es obligatoria")
    @Positive(message = "La capacidad debe ser mayor a cero")
    private Integer capacidadMaxima;

    @NotNull(message = "El costo por hora es obligatorio")
    @Positive(message = "El costo por hora debe ser mayor a cero")
    private Double costoPorHora;

    @NotNull(message = "La sucursal es obligatoria")
    private Long sucursalId;

    @NotNull(message = "El gestor es obligatorio")
    private Long gestorId;
}

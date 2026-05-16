package com.reservas.reservas_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class SalonResponse {
    private Long id;
    private String nombre;
    private Integer capacidadMaxima;
    private Double costoPorHora;
    private SucursalResponse sucursal;
    private UsuarioResponse gestor;
}
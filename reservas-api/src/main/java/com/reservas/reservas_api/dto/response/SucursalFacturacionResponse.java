package com.reservas.reservas_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class SucursalFacturacionResponse {
    private Long sucursalId;
    private String nombreSucursal;
    private Double totalFacturado;
}
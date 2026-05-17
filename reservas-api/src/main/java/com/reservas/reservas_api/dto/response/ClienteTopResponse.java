package com.reservas.reservas_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ClienteTopResponse {
    private String documentoCliente;
    private String nombreCliente;
    private Long totalReservas;
}
package com.reservas.reservas_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class SucursalResponse {
    private Long id;
    private String nombre;
    private String direccion;
    private UsuarioResponse gestor;
}
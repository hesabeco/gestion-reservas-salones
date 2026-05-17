package com.reservas.reservas_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class GananciasResponse {
    private Double hoy;
    private Double estaSemana;
    private Double esteMes;
    private Double esteAnio;
}

package com.reservas.reservas_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ReservaResponse {
    private Long id;
    private String documentoCliente;
    private String nombreCliente;
    private SalonResponse salon;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFinEstimada;
    private LocalDateTime fechaCreacion;
    private Integer asistentes;
    private String estado;
    private String motivoRechazo;
}
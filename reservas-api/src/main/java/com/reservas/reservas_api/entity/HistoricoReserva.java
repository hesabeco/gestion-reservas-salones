package com.reservas.reservas_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historico_reservas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoricoReserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long reservaId;

    @Column(nullable = false, length = 12)
    private String documentoCliente;

    @Column(nullable = false)
    private String nombreCliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salon_id", nullable = false)
    private Salon salon;

    @Column(nullable = false)
    private LocalDateTime fechaInicio;

    @Column(nullable = false)
    private LocalDateTime fechaFinEstimada;

    @Column(nullable = false)
    private LocalDateTime fechaFinReal;

    @Column(nullable = false)
    private Integer asistentes;

    @Column(nullable = false)
    private Double totalCobrado;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;
}

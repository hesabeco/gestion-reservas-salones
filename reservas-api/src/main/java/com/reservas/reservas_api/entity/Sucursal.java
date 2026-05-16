package com.reservas.reservas_api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sucursales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sucursal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String direccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gestor_id", nullable = false)
    private Usuario gestor;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;
}

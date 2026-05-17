package com.reservas.reservas_api.repository;

import com.reservas.reservas_api.entity.HistoricoReserva;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricoReservaRepository extends JpaRepository<HistoricoReserva, Long> {
}
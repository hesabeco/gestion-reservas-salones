package com.reservas.reservas_api.repository;

import com.reservas.reservas_api.entity.EstadoReserva;
import com.reservas.reservas_api.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    boolean existsByDocumentoClienteAndEstado(String documentoCliente, EstadoReserva estado);

    Optional<Reserva> findByDocumentoClienteAndSalonIdAndEstado(
            String documentoCliente, Long salonId, EstadoReserva estado);

    List<Reserva> findBySalonIdAndEstado(Long salonId, EstadoReserva estado);

    List<Reserva> findByDocumentoClienteContainingAndEstado(
            String documento, EstadoReserva estado);

    @Query("""
        SELECT r FROM Reserva r
        WHERE r.salon.id = :salonId
        AND r.estado = 'ACTIVA'
        AND r.fechaInicio < :fechaFin
        AND r.fechaFinEstimada > :fechaInicio
    """)
    List<Reserva> findReservasSolapadas(
            @Param("salonId") Long salonId,
            @Param("fechaInicio") java.time.LocalDateTime fechaInicio,
            @Param("fechaFin") java.time.LocalDateTime fechaFin);

    boolean existsByDocumentoClienteAndSalonIdAndEstado(
            String documentoCliente, Long salonId, EstadoReserva estado);
}
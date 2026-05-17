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

    // Top 10 clientes con más reservas activas global
    @Query("""
                SELECT r.documentoCliente, r.nombreCliente, COUNT(r) as total
                FROM Reserva r
                GROUP BY r.documentoCliente, r.nombreCliente
                ORDER BY total DESC
                LIMIT 10
            """)
    List<Object[]> findTop10ClientesGlobal();

    // Top 10 clientes con más reservas activas por salón específico
    @Query("""
                SELECT r.documentoCliente, r.nombreCliente, COUNT(r) as total
                FROM Reserva r
                WHERE r.salon.id = :salonId
                GROUP BY r.documentoCliente, r.nombreCliente
                ORDER BY total DESC
                LIMIT 10
            """)
    List<Object[]> findTop10ClientesBySalon(@Param("salonId") Long salonId);
}
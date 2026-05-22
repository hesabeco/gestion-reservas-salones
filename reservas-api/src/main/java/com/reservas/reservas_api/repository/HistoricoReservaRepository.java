package com.reservas.reservas_api.repository;

import com.reservas.reservas_api.entity.HistoricoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface HistoricoReservaRepository extends JpaRepository<HistoricoReserva, Long> {
    // Suma total cobrado en un salón en un período de tiempo
    @Query("""
    SELECT COALESCE(SUM(h.totalCobrado), 0)
    FROM HistoricoReserva h
    WHERE h.salon.id = :salonId
    AND h.fechaFinReal BETWEEN :inicio AND :fin
""")
    Double sumTotalCobradoBySalonAndPeriodo(
            @Param("salonId") Long salonId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    // Top 3 sucursales con mayor facturación en un período
    @Query("""
    SELECT h.salon.sucursal.id, h.salon.sucursal.nombre, SUM(h.totalCobrado) as total
    FROM HistoricoReserva h
    WHERE h.fechaFinReal BETWEEN :inicio AND :fin
    GROUP BY h.salon.sucursal.id, h.salon.sucursal.nombre
    ORDER BY total DESC
    LIMIT 3
""")
    List<Object[]> findTop3SucursalesByFacturacion(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    // Top 10 clientes con más reservas en histórico global
    @Query("""
    SELECT h.documentoCliente, h.nombreCliente, COUNT(h) as total
    FROM HistoricoReserva h
    GROUP BY h.documentoCliente, h.nombreCliente
    ORDER BY total DESC
    LIMIT 10
""")
    List<Object[]> findTop10ClientesGlobal();

    // Top 10 clientes con más reservas en histórico por salón
    @Query("""
    SELECT h.documentoCliente, h.nombreCliente, COUNT(h) as total
    FROM HistoricoReserva h
    WHERE h.salon.id = :salonId
    GROUP BY h.documentoCliente, h.nombreCliente
    ORDER BY total DESC
    LIMIT 10
""")
    List<Object[]> findTop10ClientesBySalon(@Param("salonId") Long salonId);

    boolean existsByDocumentoClienteAndSalonId(String documentoCliente, Long salonId);
}
package com.reservas.reservas_api.repository;

import com.reservas.reservas_api.entity.Salon;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SalonRepository extends JpaRepository<Salon, Long> {
    List<Salon> findBySucursalId(Long sucursalId);
    List<Salon> findByGestorId(Long gestorId);
    boolean existsByNombreAndSucursalId(String nombre, Long sucursalId);
    boolean existsByNombreAndSucursalIdAndIdNot(String nombre, Long sucursalId, Long id);
    boolean existsBySucursalIdAndActivoTrue(Long sucursalId);

}

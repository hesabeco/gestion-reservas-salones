package com.reservas.reservas_api.repository;

import com.reservas.reservas_api.entity.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SucursalRepository extends JpaRepository<Sucursal, Long> {
    List<Sucursal> findByGestorId(Long gestorId);
    boolean existsByNombre(String nombre);
    boolean existsByNombreAndIdNot(String nombre, Long id);

}
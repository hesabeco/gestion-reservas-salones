package com.reservas.reservas_api.service.impl;

import com.reservas.reservas_api.dto.request.FinalizarReservaRequest;
import com.reservas.reservas_api.dto.request.RechazarReservaRequest;
import com.reservas.reservas_api.dto.request.ReservaRequest;
import com.reservas.reservas_api.dto.response.FinalizarReservaResponse;
import com.reservas.reservas_api.dto.response.ReservaResponse;
import com.reservas.reservas_api.dto.response.SalonResponse;
import com.reservas.reservas_api.dto.response.SucursalResponse;
import com.reservas.reservas_api.dto.response.UsuarioResponse;
import com.reservas.reservas_api.entity.*;
import com.reservas.reservas_api.exception.BusinessException;
import com.reservas.reservas_api.exception.ResourceNotFoundException;
import com.reservas.reservas_api.repository.*;
import com.reservas.reservas_api.service.NotificacionService;
import com.reservas.reservas_api.service.ReservaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final HistoricoReservaRepository historicoReservaRepository;
    private final SalonRepository salonRepository;
    private final NotificacionService notificacionService;

    @Override
    @Transactional
    public Map<String, Long> registrar(ReservaRequest request) {
        // Valida fecha fin posterior a fecha inicio
        if (!request.getFechaFinEstimada().isAfter(request.getFechaInicio())) {
            throw new BusinessException("La fecha de fin debe ser posterior a la fecha de inicio");
        }

        // Valida que el cliente no tenga reserva activa en ningún salón
        if (reservaRepository.existsByDocumentoClienteAndEstado(
                request.getDocumentoCliente(), EstadoReserva.ACTIVA)) {
            throw new BusinessException(
                    "No se puede Registrar Reserva, ya existe una reserva activa para este documento en este u otro salón");
        }

        // Obtiene salón
        Salon salon = salonRepository.findById(request.getSalonId())
                .orElseThrow(() -> new ResourceNotFoundException("Salón no encontrado"));

        // Valida que el salón y sucursal estén activos
        if (!salon.getActivo()) {
            throw new BusinessException("El salón no está activo");
        }
        if (!salon.getSucursal().getActivo()) {
            throw new BusinessException("La sucursal no está activa");
        }

        // Valida capacidad disponible en el rango horario
        List<Reserva> solapadas = reservaRepository.findReservasSolapadas(
                salon.getId(), request.getFechaInicio(), request.getFechaFinEstimada());

        int asistentesComprometidos = solapadas.stream()
                .mapToInt(Reserva::getAsistentes)
                .sum();

        if (asistentesComprometidos + request.getAsistentes() > salon.getCapacidadMaxima()) {
            throw new BusinessException(
                    "No se puede Registrar Reserva, capacidad insuficiente en el salón");
        }

        // Calcula el costo estimado para determinar estado
        long minutos = ChronoUnit.MINUTES.between(request.getFechaInicio(), request.getFechaFinEstimada());
        long horas = (minutos + 59) / 60;
        double costoEstimado = horas * salon.getCostoPorHora();

        EstadoReserva estado = costoEstimado > 500000
                ? EstadoReserva.PENDIENTE_APROBACION
                : EstadoReserva.ACTIVA;

        Reserva reserva = Reserva.builder()
                .documentoCliente(request.getDocumentoCliente())
                .nombreCliente(request.getNombreCliente())
                .salon(salon)
                .fechaInicio(request.getFechaInicio())
                .fechaFinEstimada(request.getFechaFinEstimada())
                .fechaCreacion(LocalDateTime.now())
                .asistentes(request.getAsistentes())
                .estado(estado)
                .build();

        reservaRepository.save(reserva);
        log.info("Reserva registrada con estado {}: id={}", estado, reserva.getId());

        return Map.of("id", reserva.getId());
    }

    @Override
    @Transactional
    public FinalizarReservaResponse finalizar(FinalizarReservaRequest request) {
        Reserva reserva = reservaRepository
                .findByDocumentoClienteAndSalonIdAndEstado(
                        request.getDocumentoCliente(),
                        request.getSalonId(),
                        EstadoReserva.ACTIVA)
                .orElseThrow(() -> new BusinessException(
                        "No se puede Finalizar Reserva, no existe una reserva activa para este documento en el salón"));

        LocalDateTime fechaFinReal = LocalDateTime.now();

        if (fechaFinReal.isBefore(reserva.getFechaInicio())) {
            throw new BusinessException(
                    "No se puede finalizar una reserva que aún no ha iniciado");
        }

        // Calcula horas efectivas redondeando hacia arriba
        long minutos = ChronoUnit.MINUTES.between(reserva.getFechaInicio(), fechaFinReal);
        long horas = (minutos + 59) / 60;
        double totalCobrado = horas * reserva.getSalon().getCostoPorHora();

        // Mover a histórico
        HistoricoReserva historico = HistoricoReserva.builder()
                .reservaId(reserva.getId())
                .documentoCliente(reserva.getDocumentoCliente())
                .nombreCliente(reserva.getNombreCliente())
                .salon(reserva.getSalon())
                .fechaInicio(reserva.getFechaInicio())
                .fechaFinEstimada(reserva.getFechaFinEstimada())
                .fechaFinReal(fechaFinReal)
                .asistentes(reserva.getAsistentes())
                .totalCobrado(totalCobrado)
                .fechaCreacion(reserva.getFechaCreacion())
                .build();

        historicoReservaRepository.save(historico);
        reservaRepository.delete(reserva);

        log.info("Reserva finalizada: id={}, totalCobrado={}", reserva.getId(), totalCobrado);

        return FinalizarReservaResponse.builder()
                .mensaje("Reserva finalizada")
                .totalCobrado(totalCobrado)
                .build();
    }

    @Override
    public List<ReservaResponse> obtenerActivasPorSalon(Long salonId) {
        return reservaRepository.findBySalonIdAndEstado(salonId, EstadoReserva.ACTIVA)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ReservaResponse> buscarPorDocumento(String documento) {
        return reservaRepository.findByDocumentoClienteContainingAndEstado(
                        documento, EstadoReserva.ACTIVA)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void aprobar(Long id) {
        Reserva reserva = obtenerReserva(id);

        // Verifica que esté en estado PENDIENTE_APROBACION
        verificarEstadoPendiente(reserva);

        // Cambia estado a ACTIVA
        reserva.setEstado(EstadoReserva.ACTIVA);
        reservaRepository.save(reserva);

        // Notifica al gestor responsable del salón via microservicio
        notificacionService.enviarNotificacion(
                reserva.getSalon().getGestor().getEmail(),
                reserva.getDocumentoCliente(),
                "Su reserva ha sido aprobada",
                String.valueOf(reserva.getSalon().getId())
        );

        log.info("Reserva aprobada: id={}", id);
    }

    @Override
    @Transactional
    public void rechazar(Long id, RechazarReservaRequest request) {
        Reserva reserva = obtenerReserva(id);

        // Verifica que esté en estado PENDIENTE_APROBACION
        verificarEstadoPendiente(reserva);

        // Cambia estado a RECHAZADA con motivo obligatorio
        reserva.setEstado(EstadoReserva.RECHAZADA);
        reserva.setMotivoRechazo(request.getMotivo());
        reservaRepository.save(reserva);

        log.info("Reserva rechazada: id={}, motivo={}", id, request.getMotivo());
    }

    @Override
    public void verificarExpiracion() {
        // Busca todas las reservas en PENDIENTE_APROBACION con más de 48 y las marca automáticamente como EXPIRADA
        reservaRepository.findAll()
                .stream()
                .filter(r -> r.getEstado() == EstadoReserva.PENDIENTE_APROBACION
                        && r.getFechaCreacion().isBefore(LocalDateTime.now().minusHours(48)))
                .forEach(r -> {
                    r.setEstado(EstadoReserva.EXPIRADA);
                    reservaRepository.save(r);
                    log.warn("Reserva expirada automáticamente: id={}", r.getId());
                });
    }

    // Obtiene la reserva o lanzar error
    private Reserva obtenerReserva(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));
    }

    //Verifica que la reserva esté en PENDIENTE_APROBACION
    private void verificarEstadoPendiente(Reserva reserva) {
        if (reserva.getEstado() != EstadoReserva.PENDIENTE_APROBACION) {
            throw new BusinessException("La reserva no está en estado PENDIENTE_APROBACION");
        }
    }

    public ReservaResponse mapToResponse(Reserva reserva) {
        return ReservaResponse.builder()
                .id(reserva.getId())
                .documentoCliente(reserva.getDocumentoCliente())
                .nombreCliente(reserva.getNombreCliente())
                .salon(SalonResponse.builder()
                        .id(reserva.getSalon().getId())
                        .nombre(reserva.getSalon().getNombre())
                        .capacidadMaxima(reserva.getSalon().getCapacidadMaxima())
                        .costoPorHora(reserva.getSalon().getCostoPorHora())
                        .sucursal(SucursalResponse.builder()
                                .id(reserva.getSalon().getSucursal().getId())
                                .nombre(reserva.getSalon().getSucursal().getNombre())
                                .direccion(reserva.getSalon().getSucursal().getDireccion())
                                .build())
                        .gestor(UsuarioResponse.builder()
                                .id(reserva.getSalon().getGestor().getId())
                                .nombre(reserva.getSalon().getGestor().getNombre())
                                .email(reserva.getSalon().getGestor().getEmail())
                                .rol(reserva.getSalon().getGestor().getRol().name())
                                .build())
                        .build())
                .fechaInicio(reserva.getFechaInicio())
                .fechaFinEstimada(reserva.getFechaFinEstimada())
                .fechaCreacion(reserva.getFechaCreacion())
                .asistentes(reserva.getAsistentes())
                .estado(reserva.getEstado().name())
                .motivoRechazo(reserva.getMotivoRechazo())
                .build();
    }
}
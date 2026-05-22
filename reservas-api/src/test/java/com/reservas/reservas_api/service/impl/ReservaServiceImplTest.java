package com.reservas.reservas_api.service.impl;

import com.reservas.reservas_api.dto.request.FinalizarReservaRequest;
import com.reservas.reservas_api.dto.request.ReservaRequest;
import com.reservas.reservas_api.entity.*;
import com.reservas.reservas_api.exception.BusinessException;
import com.reservas.reservas_api.repository.HistoricoReservaRepository;
import com.reservas.reservas_api.repository.ReservaRepository;
import com.reservas.reservas_api.repository.SalonRepository;
import com.reservas.reservas_api.service.NotificacionService;
import com.reservas.reservas_api.util.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceImplTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private HistoricoReservaRepository historicoReservaRepository;

    @Mock
    private SalonRepository salonRepository;

    @Mock
    private NotificacionService notificacionService;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private ReservaServiceImpl reservaService;

    private Salon salon;
    private Sucursal sucursal;
    private Usuario gestor;
    private ReservaRequest reservaRequest;
    private FinalizarReservaRequest finalizarRequest;

    @BeforeEach
    void setUp() {
        gestor = Usuario.builder()
                .id(1L)
                .nombre("Carlos Pérez")
                .email("carlos.perez@eventos.com")
                .rol(Rol.GESTOR)
                .build();

        sucursal = Sucursal.builder()
                .id(1L)
                .nombre("Eventos Bogotá")
                .direccion("Cra 7 # 32-16")
                .activo(true)
                .gestor(gestor)
                .build();

        salon = Salon.builder()
                .id(1L)
                .nombre("Salon Ejecutivo")
                .capacidadMaxima(25)
                .costoPorHora(50000.0)
                .activo(true)
                .sucursal(sucursal)
                .gestor(gestor)
                .build();

        reservaRequest = new ReservaRequest();
        reservaRequest.setDocumentoCliente("123456789");
        reservaRequest.setNombreCliente("Cliente Prueba");
        reservaRequest.setSalonId(1L);
        reservaRequest.setFechaInicio(LocalDateTime.now().minusHours(2));
        reservaRequest.setFechaFinEstimada(LocalDateTime.now().plusHours(1));
        reservaRequest.setAsistentes(10);

        finalizarRequest = new FinalizarReservaRequest();
        finalizarRequest.setDocumentoCliente("123456789");
        finalizarRequest.setSalonId(1L);
    }

    @Test
    void registrar_reserva_exitosa() {
        // Arrange
        when(reservaRepository.existsByDocumentoClienteAndEstado(
                "123456789", EstadoReserva.ACTIVA)).thenReturn(false);

        when(securityUtils.obtenerSalonConValidacionAcceso(eq(1L), anyString()))
                .thenReturn(salon);

        when(reservaRepository.findReservasSolapadas(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        when(reservaRepository.save(any(Reserva.class)))
                .thenAnswer(invocation -> {
                    Reserva r = invocation.getArgument(0);
                    r.setId(1L);
                    return r;
                });

        // Act
        Map<String, Long> response = reservaService.registrar(reservaRequest);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.get("id"));
        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    void registrar_clienteConReservaActiva_lanzaExcepcion() {
        // Arrange
        when(reservaRepository.existsByDocumentoClienteAndEstado(
                "123456789", EstadoReserva.ACTIVA)).thenReturn(true);

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class,
                () -> reservaService.registrar(reservaRequest));

        assertTrue(ex.getMessage().contains("ya existe una reserva activa"));
        verify(reservaRepository, never()).save(any());
        verify(securityUtils, never()).obtenerSalonConValidacionAcceso(any(), anyString());
    }

    @Test
    void registrar_capacidadInsuficiente_lanzaExcepcion() {
        // Arrange
        when(reservaRepository.existsByDocumentoClienteAndEstado(
                "123456789", EstadoReserva.ACTIVA)).thenReturn(false);

        when(securityUtils.obtenerSalonConValidacionAcceso(eq(1L), anyString()))
                .thenReturn(salon);

        when(reservaRepository.findReservasSolapadas(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        reservaRequest.setAsistentes(30); // 30 > 25 capacidad

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class,
                () -> reservaService.registrar(reservaRequest));

        assertTrue(ex.getMessage().contains("capacidad insuficiente"));
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void registrar_salonInactivo_lanzaExcepcion() {
        // Arrange
        salon.setActivo(false);

        when(reservaRepository.existsByDocumentoClienteAndEstado(
                "123456789", EstadoReserva.ACTIVA)).thenReturn(false);

        when(securityUtils.obtenerSalonConValidacionAcceso(eq(1L), anyString()))
                .thenReturn(salon);

        // Act & Assert
        assertThrows(BusinessException.class,
                () -> reservaService.registrar(reservaRequest));

        verify(reservaRepository, never()).save(any());
    }

    @Test
    void finalizar_reserva_exitosa() {
        // Arrange
        Reserva reservaActiva = Reserva.builder()
                .id(1L)
                .documentoCliente("123456789")
                .nombreCliente("Cliente Prueba")
                .salon(salon)
                .fechaInicio(LocalDateTime.now().minusHours(2))
                .fechaFinEstimada(LocalDateTime.now().plusHours(1))
                .fechaCreacion(LocalDateTime.now().minusHours(2))
                .asistentes(10)
                .estado(EstadoReserva.ACTIVA)
                .build();

        when(reservaRepository.findByDocumentoClienteAndSalonIdAndEstado(
                "123456789", 1L, EstadoReserva.ACTIVA))
                .thenReturn(Optional.of(reservaActiva));

        // Act
        var response = reservaService.finalizar(finalizarRequest);

        // Assert
        assertNotNull(response);
        assertEquals("Reserva finalizada", response.getMensaje());
        assertTrue(response.getTotalCobrado() > 0);
        verify(securityUtils).validarAccesoASalon(any(Salon.class), anyString());
        verify(historicoReservaRepository).save(any());
        verify(reservaRepository).delete(reservaActiva);
    }

    @Test
    void finalizar_reservaNoExiste_lanzaExcepcion() {
        // Arrange
        when(reservaRepository.findByDocumentoClienteAndSalonIdAndEstado(
                "123456789", 1L, EstadoReserva.ACTIVA))
                .thenReturn(Optional.empty());

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class,
                () -> reservaService.finalizar(finalizarRequest));

        assertTrue(ex.getMessage().contains("No se puede Finalizar Reserva"));
        verify(historicoReservaRepository, never()).save(any());
        verify(securityUtils, never()).validarAccesoASalon(any(), anyString());
    }
}
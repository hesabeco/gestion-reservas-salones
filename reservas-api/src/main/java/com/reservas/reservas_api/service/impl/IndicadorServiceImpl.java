package com.reservas.reservas_api.service.impl;

import com.reservas.reservas_api.dto.response.ClienteTopResponse;
import com.reservas.reservas_api.dto.response.GananciasResponse;
import com.reservas.reservas_api.dto.response.SucursalFacturacionResponse;
import com.reservas.reservas_api.entity.EstadoReserva;
import com.reservas.reservas_api.repository.HistoricoReservaRepository;
import com.reservas.reservas_api.repository.ReservaRepository;
import com.reservas.reservas_api.service.IndicadorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class IndicadorServiceImpl implements IndicadorService {

    private final ReservaRepository reservaRepository;
    private final HistoricoReservaRepository historicoReservaRepository;

    @Override
    public List<ClienteTopResponse> top10ClientesGlobal() {
        // Combina reservas activas + histórico y agrupa por documento
        // para obtener el total real de reservas de cada cliente
        Map<String, ClienteTopResponse> mapa = new java.util.HashMap<>();

        // Agrega reservas activas
        reservaRepository.findTop10ClientesGlobal().forEach(r -> {
            String doc = (String) r[0];
            mapa.put(doc, ClienteTopResponse.builder()
                    .documentoCliente(doc)
                    .nombreCliente((String) r[1])
                    .totalReservas((Long) r[2])
                    .build());
        });

        // Agrega o suma reservas del histórico
        historicoReservaRepository.findTop10ClientesGlobal().forEach(r -> {
            String doc = (String) r[0];
            if (mapa.containsKey(doc)) {
                mapa.get(doc).setTotalReservas(
                        mapa.get(doc).getTotalReservas() + (Long) r[2]);
            } else {
                mapa.put(doc, ClienteTopResponse.builder()
                        .documentoCliente(doc)
                        .nombreCliente((String) r[1])
                        .totalReservas((Long) r[2])
                        .build());
            }
        });

        // Ordena por total descendente y retorna top 10
        return mapa.values().stream()
                .sorted((a, b) -> Long.compare(b.getTotalReservas(), a.getTotalReservas()))
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClienteTopResponse> top10ClientesPorSalon(Long salonId) {
        // Combina reservas activas + histórico para un salón específico
        Map<String, ClienteTopResponse> mapa = new java.util.HashMap<>();

        // Agrega reservas activas del salón
        reservaRepository.findTop10ClientesBySalon(salonId).forEach(r -> {
            String doc = (String) r[0];
            mapa.put(doc, ClienteTopResponse.builder()
                    .documentoCliente(doc)
                    .nombreCliente((String) r[1])
                    .totalReservas((Long) r[2])
                    .build());
        });

        // Agrega o suma reservas del histórico del salón
        historicoReservaRepository.findTop10ClientesBySalon(salonId).forEach(r -> {
            String doc = (String) r[0];
            if (mapa.containsKey(doc)) {
                mapa.get(doc).setTotalReservas(
                        mapa.get(doc).getTotalReservas() + (Long) r[2]);
            } else {
                mapa.put(doc, ClienteTopResponse.builder()
                        .documentoCliente(doc)
                        .nombreCliente((String) r[1])
                        .totalReservas((Long) r[2])
                        .build());
            }
        });

        // Ordena por total descendente y retorna top 10
        return mapa.values().stream()
                .sorted((a, b) -> Long.compare(b.getTotalReservas(), a.getTotalReservas()))
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClienteTopResponse> primeraVezEnSalon(Long salonId) {
        // Retorna reservas activas de clientes que nunca han reservado
        // antes en ese salón (no tienen registros en el histórico)
        return reservaRepository.findBySalonIdAndEstado(salonId, EstadoReserva.ACTIVA)
                .stream()
                .filter(r -> {
                    // Verifica que el cliente no tenga reservas anteriores en el histórico
                    long totalHistorico = historicoReservaRepository
                            .findTop10ClientesBySalon(salonId)
                            .stream()
                            .filter(h -> h[0].equals(r.getDocumentoCliente()))
                            .count();
                    return totalHistorico == 0;
                })
                .map(r -> ClienteTopResponse.builder()
                        .documentoCliente(r.getDocumentoCliente())
                        .nombreCliente(r.getNombreCliente())
                        .totalReservas(1L)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public GananciasResponse obtenerGanancias(Long salonId) {
        LocalDateTime ahora = LocalDateTime.now();

        // Período de hoy: desde las 00:00 hasta las 23:59
        LocalDateTime inicioDia = ahora.toLocalDate().atStartOfDay();
        LocalDateTime finDia = inicioDia.plusDays(1);

        // Período de esta semana: desde el lunes hasta el domingo
        LocalDateTime inicioSemana = ahora.toLocalDate()
                .with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime finSemana = inicioSemana.plusWeeks(1);

        // Período de este mes: desde el día 1 hasta el último día
        LocalDateTime inicioMes = ahora.toLocalDate()
                .with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
        LocalDateTime finMes = inicioMes.plusMonths(1);

        // Período de este año: desde el 1 de enero hasta el 31 de diciembre
        LocalDateTime inicioAnio = ahora.toLocalDate()
                .with(TemporalAdjusters.firstDayOfYear()).atStartOfDay();
        LocalDateTime finAnio = inicioAnio.plusYears(1);

        log.info("Calculando ganancias para salón: {}", salonId);

        return GananciasResponse.builder()
                .hoy(historicoReservaRepository
                        .sumTotalCobradoBySalonAndPeriodo(salonId, inicioDia, finDia))
                .estaSemana(historicoReservaRepository
                        .sumTotalCobradoBySalonAndPeriodo(salonId, inicioSemana, finSemana))
                .esteMes(historicoReservaRepository
                        .sumTotalCobradoBySalonAndPeriodo(salonId, inicioMes, finMes))
                .esteAnio(historicoReservaRepository
                        .sumTotalCobradoBySalonAndPeriodo(salonId, inicioAnio, finAnio))
                .build();
    }

    @Override
    public List<SucursalFacturacionResponse> top3SucursalesMes() {
        // Top 3 sucursales con mayor facturación en el mes actual
        LocalDateTime inicioMes = LocalDateTime.now().toLocalDate()
                .with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
        LocalDateTime finMes = inicioMes.plusMonths(1);

        log.info("Calculando top 3 sucursales del mes");

        return historicoReservaRepository
                .findTop3SucursalesByFacturacion(inicioMes, finMes)
                .stream()
                .map(r -> SucursalFacturacionResponse.builder()
                        .sucursalId((Long) r[0])
                        .nombreSucursal((String) r[1])
                        .totalFacturado((Double) r[2])
                        .build())
                .collect(Collectors.toList());
    }
}
package com.reservas.reservas_api.config;

import com.reservas.reservas_api.service.ReservaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class ExpiracionReservasTask {

    private final ReservaService reservaService;

    // Ejecuta cada hora para verificar reservas expiradas
    @Scheduled(fixedRate = 3600000)
    public void verificarExpiracion() {
        log.info("Verificando reservas expiradas...");
        reservaService.verificarExpiracion();
    }
}
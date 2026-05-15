package com.reservas.reservas_api.config;

import com.reservas.reservas_api.entity.Rol;
import com.reservas.reservas_api.entity.Usuario;
import com.reservas.reservas_api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!usuarioRepository.existsByEmail("admin@mail.com")) {
            Usuario admin = Usuario.builder()
                    .nombre("Administrador")
                    .email("admin@mail.com")
                    .contrasenia(passwordEncoder.encode("admin"))
                    .rol(Rol.ADMIN)
                    .build();
            usuarioRepository.save(admin);
            log.info("Usuario admin creado exitosamente");
        }
    }
}
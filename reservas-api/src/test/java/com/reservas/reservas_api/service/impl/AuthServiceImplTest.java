package com.reservas.reservas_api.service.impl;

import com.reservas.reservas_api.config.JwtUtil;
import com.reservas.reservas_api.dto.request.LoginRequest;
import com.reservas.reservas_api.dto.request.RegistroUsuarioRequest;
import com.reservas.reservas_api.dto.response.LoginResponse;
import com.reservas.reservas_api.dto.response.UsuarioResponse;
import com.reservas.reservas_api.entity.Rol;
import com.reservas.reservas_api.entity.Usuario;
import com.reservas.reservas_api.exception.BusinessException;
import com.reservas.reservas_api.exception.ResourceNotFoundException;
import com.reservas.reservas_api.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private Usuario usuarioAdmin;
    private LoginRequest loginRequest;
    private RegistroUsuarioRequest registroRequest;

    @BeforeEach
    void setUp() {
        usuarioAdmin = Usuario.builder()
                .id(1L)
                .nombre("Administrador")
                .email("admin@mail.com")
                .contrasenia("$2a$12$hashedpassword")
                .rol(Rol.ADMIN)
                .build();

        loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@mail.com");
        loginRequest.setContrasenia("admin");

        registroRequest = new RegistroUsuarioRequest();
        registroRequest.setNombre("Nuevo Gestor");
        registroRequest.setEmail("gestor@mail.com");
        registroRequest.setContrasenia("gestor123");
    }

    @Test
    void login_exitoso() {
        // Arrange
        when(usuarioRepository.findByEmail("admin@mail.com"))
                .thenReturn(Optional.of(usuarioAdmin));
        when(passwordEncoder.matches("admin", usuarioAdmin.getContrasenia()))
                .thenReturn(true);
        when(jwtUtil.generateToken("admin@mail.com", "ADMIN"))
                .thenReturn("token.jwt.generado");

        // Act
        LoginResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("token.jwt.generado", response.getToken());
        assertEquals("admin@mail.com", response.getEmail());
        assertEquals("ADMIN", response.getRol());
        verify(usuarioRepository).findByEmail("admin@mail.com");
        verify(passwordEncoder).matches("admin", usuarioAdmin.getContrasenia());
    }

    @Test
    void login_usuarioNoEncontrado_lanzaExcepcion() {
        // Arrange
        when(usuarioRepository.findByEmail("noexiste@mail.com"))
                .thenReturn(Optional.empty());

        loginRequest.setEmail("noexiste@mail.com");

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> authService.login(loginRequest));
        verify(usuarioRepository).findByEmail("noexiste@mail.com");
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void login_contrasenaIncorrecta_lanzaExcepcion() {
        // Arrange
        when(usuarioRepository.findByEmail("admin@mail.com"))
                .thenReturn(Optional.of(usuarioAdmin));
        when(passwordEncoder.matches("wrongpassword", usuarioAdmin.getContrasenia()))
                .thenReturn(false);

        loginRequest.setContrasenia("wrongpassword");

        // Act & Assert
        assertThrows(BusinessException.class,
                () -> authService.login(loginRequest));
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void register_exitoso() {
        // Arrange
        when(usuarioRepository.existsByEmail("gestor@mail.com"))
                .thenReturn(false);
        when(passwordEncoder.encode("gestor123"))
                .thenReturn("$2a$12$hashedgestor");
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> {
                    Usuario u = invocation.getArgument(0);
                    u.setId(2L);
                    return u;
                });

        // Act
        UsuarioResponse response = authService.register(registroRequest);

        // Assert
        assertNotNull(response);
        assertEquals("gestor@mail.com", response.getEmail());
        assertEquals("GESTOR", response.getRol());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void register_emailDuplicado_lanzaExcepcion() {
        // Arrange
        when(usuarioRepository.existsByEmail("gestor@mail.com"))
                .thenReturn(true);

        // Act & Assert
        assertThrows(BusinessException.class,
                () -> authService.register(registroRequest));
        verify(usuarioRepository, never()).save(any());
    }
}
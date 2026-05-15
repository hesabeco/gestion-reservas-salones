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
import com.reservas.reservas_api.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getContrasenia(), usuario.getContrasenia())) {
            throw new BusinessException("Credenciales incorrectas");
        }

        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getRol().name());
        log.info("Login exitoso para usuario: {}", usuario.getEmail());

        return LoginResponse.builder()
                .token(token)
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .rol(usuario.getRol().name())
                .build();
    }

    @Override
    public UsuarioResponse register(RegistroUsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Ya existe un usuario con ese email");
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .contrasenia(passwordEncoder.encode(request.getContrasenia()))
                .rol(Rol.GESTOR)
                .build();

        usuarioRepository.save(usuario);
        log.info("Usuario GESTOR creado: {}", usuario.getEmail());

        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol().name())
                .build();
    }

    @Override
    public void logout(String token) {
        jwtUtil.invalidateToken(token);
        log.info("Token invalidado exitosamente");
    }
}
package com.reservas.reservas_api.service;

import com.reservas.reservas_api.dto.request.LoginRequest;
import com.reservas.reservas_api.dto.request.RegistroUsuarioRequest;
import com.reservas.reservas_api.dto.response.LoginResponse;
import com.reservas.reservas_api.dto.response.UsuarioResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    UsuarioResponse register(RegistroUsuarioRequest request);
    void logout(String token);
}
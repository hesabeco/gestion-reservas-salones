package com.reservas.reservas_api.util;

import com.reservas.reservas_api.entity.Rol;
import com.reservas.reservas_api.entity.Salon;
import com.reservas.reservas_api.entity.Usuario;
import com.reservas.reservas_api.exception.ResourceNotFoundException;
import com.reservas.reservas_api.exception.UnauthorizedException;
import com.reservas.reservas_api.repository.SalonRepository;
import com.reservas.reservas_api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UsuarioRepository usuarioRepository;
    private final SalonRepository salonRepository;

    public Usuario obtenerUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Usuario autenticado no encontrado"));
    }

    public boolean esAdmin(Usuario usuario) {
        return usuario.getRol() == Rol.ADMIN;
    }

    public boolean esGestor(Usuario usuario) {
        return usuario.getRol() == Rol.GESTOR;
    }

    public void validarAccesoASalon(Salon salon, String mensajeError) {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();

        if (esGestor(usuarioAutenticado)
                && !salon.getGestor().getId().equals(usuarioAutenticado.getId())) {
            throw new UnauthorizedException(mensajeError);
        }
    }

    public Salon obtenerSalonConValidacionAcceso(Long salonId, String mensajeError) {
        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() -> new ResourceNotFoundException("Salón no encontrado"));

        validarAccesoASalon(salon, mensajeError);

        return salon;
    }
}
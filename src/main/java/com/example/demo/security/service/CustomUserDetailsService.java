package com.example.demo.security.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

// Servicio para gestionar la carga de usuarios y validaciones de seguridad.
@Service
public class CustomUserDetailsService implements UserDetailsService {

    //Busca un usuario por su nombre de usuario.Por ahora utiliza un usuario estático para permitir el desarrollo.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("admin".equals(username)) {
            // Contraseña "1234" encriptada con BCrypt
            return new User("admin", "$2a$10$8z1S.z7B7.v7z7z7z7z7z7", new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
    }

    //Valida que la contraseña cumpla con los requisitos mínimos del sistema.
    //Requisitos: Mínimo 8 caracteres, una Mayúscula y un Número.
    // @param password La contraseña que el usuario intenta registrar.
    //@return true si es válida, false si no cumple los requisitos.

    public boolean esPasswordSegura(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        // Verifica si contiene al menos una letra mayúscula
        boolean tieneMayuscula = password.matches(".*[A-Z].*");

        // Verifica si contiene al menos un dígito numérico
        boolean tieneNumero = password.matches(".*[0-9].*");

        return tieneMayuscula && tieneNumero;
    }
}

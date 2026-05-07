package com.example.demo.security.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    //Clase que conecta Spring Security con el sistema de almacenamiento de usuarios.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Busca al usuario en el sistema.
        // Actualmente usa un usuario estático para pruebas hasta conectar la base de datos real.
        if ("admin".equals(username)) {
            // Creamos un usuario con nombre "admin", contraseña "1234" (encriptada) y sin roles por ahora
            return new User("admin", "$2a$10$8z1S.z7B7.v7z7z7z7z7z7", new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }

    }
}
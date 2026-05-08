package com.example.demo.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  //Filtro que intercepta cada petición HTTP entrante.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 1. Obtiene el encabezado de autorización.
        final String authHeader = request.getHeader("Authorization");
        // 2. Verifica que el encabezado exista y comience con el prefijo 'Bearer '.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        // 3. (Lógica futura): Aquí se llamará al JwtService para validar el contenido.
        filterChain.doFilter(request, response);
    }
}
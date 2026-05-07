package com.example.demo.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class JwtService {

    @Value("${JWT_SECRET}") // Esto lee la clave de tu archivo .env
    private String secretKey;
//Servicio encargado de la lógica criptográfica de los JSON Web Tokens.


    public String generateToken(UserDetails userDetails) {
        // Crea un nuevo token que incluye el nombre de usuario, la fecha de emisión
        // y una fecha de expiración, firmado con una clave secreta.

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 horas
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // Aquí irán los métodos para extraer el username y validar el token luego
}
package org.example.petvission.shared.health;

// src/main/java/com/petvission/shared/health/HealthController.java

// package com.petvission.shared.health;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        try {
            // Consulta la BD para confirmar conexión activa
            String database = jdbcTemplate.queryForObject(
                    "SELECT current_database()", String.class
            );

            return ResponseEntity.ok(Map.of(
                    "status",    "OK",
                    "database",  database,
                    "message",   "Backend conectado correctamente a PostgreSQL NeonDB",
                    "timestamp", LocalDateTime.now().toString()
            ));

        } catch (Exception e) {
            // Si la BD no responde, devuelve error claro
            return ResponseEntity.status(503).body(Map.of(
                    "status",    "ERROR",
                    "database",  "Sin conexión",
                    "message",   "No se pudo conectar a la base de datos",
                    "timestamp", LocalDateTime.now().toString()
            ));
        }
    }
}

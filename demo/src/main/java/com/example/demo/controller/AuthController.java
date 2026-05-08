package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.LoginRequest;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // REGISTRO
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody AuthRequest request) {

        User user = new User(
                request.getName(),
                request.getEmail(),
                request.getPassword()
        );

        return ResponseEntity.ok(userService.register(user));
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {

        boolean ok = userService.login(request.getEmail(), request.getPassword());

        if (ok) {
            return ResponseEntity.ok("Login exitoso");
        } else {
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }
    }
}
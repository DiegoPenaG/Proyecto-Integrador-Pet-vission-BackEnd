package com.example.demo.usuario.controller;



//CONTROLADOR DE USUARIO

// MANEJA LAS PETICIONES HTTP RELACIONADAS
//CON LOS USUARIOS


import com.example.demo.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor

public class UsuarioController {

    private final UsuarioService usuarioService;

}
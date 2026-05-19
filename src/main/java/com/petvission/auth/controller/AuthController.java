// src/main/java/com/petvission/auth/controller/AuthController.java

package com.petvission.auth.controller;

import com.petvission.auth.dto.AuthRequestDto;
import com.petvission.auth.dto.AuthResponseDto;
import com.petvission.auth.dto.RegisterRequestDto;
import com.petvission.auth.service.AuthService;
import com.petvission.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponseDto>> register(
            @Valid @RequestBody RegisterRequestDto dto) {

        AuthResponseDto response = authService.register(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(
            @Valid @RequestBody AuthRequestDto dto) {

        AuthResponseDto response = authService.login(dto);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
package com.pennywise.app.controller;

import com.pennywise.app.dto.AuthRequest;
import com.pennywise.app.dto.AuthResponse;
import com.pennywise.app.dto.RegisterRequest;
import com.pennywise.app.dto.UserDTO;
import com.pennywise.app.model.User;
import com.pennywise.app.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/demo")
    public ResponseEntity<AuthResponse> demoLogin() {
        AuthResponse response = authService.login(new AuthRequest("demo", "demo123"));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        User user = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(new UserDTO(user));
    }
}

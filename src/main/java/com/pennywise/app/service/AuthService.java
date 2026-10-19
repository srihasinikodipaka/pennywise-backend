package com.pennywise.app.service;

import com.pennywise.app.dto.AuthRequest;
import com.pennywise.app.dto.AuthResponse;
import com.pennywise.app.dto.RegisterRequest;
import com.pennywise.app.model.User;
import com.pennywise.app.repository.UserRepository;
import com.pennywise.app.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    public AuthResponse login(AuthRequest request) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtils.generateToken(userDetails.getUsername());

        User user = userRepository.findByUsername(userDetails.getUsername())
                .or(() -> userRepository.findByEmail(userDetails.getUsername()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail(), user.getFullName(), user.getCurrency());
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username '" + request.getUsername() + "' is already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email '" + request.getEmail() + "' is already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName() != null ? request.getFullName().trim() : request.getUsername());
        user.setCurrency(request.getCurrency() != null ? request.getCurrency() : "₹");

        userRepository.save(user);

        String token = jwtUtils.generateToken(user.getUsername());
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail(), user.getFullName(), user.getCurrency());
    }

    public User getCurrentAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() instanceof String && auth.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("User is not authenticated");
        }

        String username = auth.getName();
        return userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database"));
    }
}

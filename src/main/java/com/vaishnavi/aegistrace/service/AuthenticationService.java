package com.vaishnavi.aegistrace.service;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vaishnavi.aegistrace.dto.AuthResponse;
import com.vaishnavi.aegistrace.dto.LoginRequest;
import com.vaishnavi.aegistrace.dto.RegisterUserRequest;
import com.vaishnavi.aegistrace.dto.UserResponse;
import com.vaishnavi.aegistrace.entity.User;
import com.vaishnavi.aegistrace.exception.DuplicateResourceException;
import com.vaishnavi.aegistrace.repository.UserRepository;
import com.vaishnavi.aegistrace.security.JwtUtil;

@Service
public class AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final long jwtExpirationMs;

    public AuthenticationService(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.jwt.expiration-ms:86400000}") long jwtExpirationMs) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtExpirationMs = jwtExpirationMs;
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        User user = userRepository.findByUsernameIgnoreCase(request.username())
                .orElseThrow(() -> new IllegalStateException("Authenticated user no longer exists"));
        log.info("Successful login for user '{}'", user.getUsername());
        return new AuthResponse(
                jwtUtil.generateToken(user.getUsername()),
                "Bearer",
                jwtExpirationMs / 1000,
                user.getUsername(),
                user.getRole());
    }

    @Transactional
    public UserResponse register(RegisterUserRequest request) {
        if (userRepository.existsByUsernameIgnoreCase(request.username())) {
            throw new DuplicateResourceException("A user with that username already exists.");
        }
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new DuplicateResourceException("A user with that email already exists.");
        }

        User user = new User();
        user.setUsername(request.username().trim());
        user.setEmail(request.email().trim().toLowerCase(Locale.ROOT));
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role().authority());
        user.setStatus("ACTIVE");

        User saved = userRepository.save(user);
        log.info("Administrator created user '{}' with role '{}'", saved.getUsername(), saved.getRole());
        return toResponse(saved);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt());
    }
}

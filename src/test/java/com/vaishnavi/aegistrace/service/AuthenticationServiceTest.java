package com.vaishnavi.aegistrace.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vaishnavi.aegistrace.dto.AuthResponse;
import com.vaishnavi.aegistrace.dto.LoginRequest;
import com.vaishnavi.aegistrace.dto.RegisterUserRequest;
import com.vaishnavi.aegistrace.dto.UserResponse;
import com.vaishnavi.aegistrace.entity.User;
import com.vaishnavi.aegistrace.exception.DuplicateResourceException;
import com.vaishnavi.aegistrace.repository.UserRepository;
import com.vaishnavi.aegistrace.security.JwtUtil;
import com.vaishnavi.aegistrace.security.UserRole;

class AuthenticationServiceTest {

    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        jwtUtil = mock(JwtUtil.class);
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        authenticationService = new AuthenticationService(
                authenticationManager, jwtUtil, userRepository, passwordEncoder, 3_600_000);
    }

    @Test
    void loginAuthenticatesCredentialsAndReturnsBearerMetadata() {
        User analyst = user("analyst", "analyst@example.com", "ROLE_ANALYST");
        when(userRepository.findByUsernameIgnoreCase("analyst")).thenReturn(Optional.of(analyst));
        when(jwtUtil.generateToken("analyst")).thenReturn("signed-token");

        AuthResponse response = authenticationService.login(new LoginRequest("analyst", "Secret123!"));

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("analyst", "Secret123!"));
        assertThat(response.token()).isEqualTo("signed-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(3600);
        assertThat(response.role()).isEqualTo("ROLE_ANALYST");
    }

    @Test
    void registerHashesPasswordAndControlsPersistedRoleAndStatus() {
        RegisterUserRequest request = new RegisterUserRequest(
                "new.analyst", "Analyst@Example.com", "StrongSecret1!", UserRole.ANALYST);
        when(passwordEncoder.encode("StrongSecret1!")).thenReturn("bcrypt-hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = authenticationService.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getPassword()).isEqualTo("bcrypt-hash");
        assertThat(saved.getEmail()).isEqualTo("analyst@example.com");
        assertThat(saved.getRole()).isEqualTo("ROLE_ANALYST");
        assertThat(saved.getStatus()).isEqualTo("ACTIVE");
        assertThat(response.username()).isEqualTo("new.analyst");
        assertThat(response.email()).isEqualTo("analyst@example.com");
    }

    @Test
    void registerRejectsDuplicateUsernameBeforeEncodingOrSaving() {
        RegisterUserRequest request = new RegisterUserRequest(
                "analyst", "other@example.com", "StrongSecret1!", UserRole.VIEWER);
        when(userRepository.existsByUsernameIgnoreCase("analyst")).thenReturn(true);

        assertThatThrownBy(() -> authenticationService.register(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("username");

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    private User user(String username, String email, String role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setRole(role);
        user.setStatus("ACTIVE");
        user.setPassword("bcrypt-hash");
        return user;
    }
}

package com.vaishnavi.aegistrace.dto;

import com.vaishnavi.aegistrace.security.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank
        @Size(min = 3, max = 50)
        @Pattern(regexp = "^[A-Za-z0-9._-]+$", message = "must contain only letters, numbers, dots, underscores, or hyphens")
        String username,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank
        @Size(min = 12, max = 128)
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$", message = "must contain a letter, number, and special character")
        String password,
        @NotNull UserRole role) {
}

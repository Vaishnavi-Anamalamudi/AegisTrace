package com.vaishnavi.aegistrace.dto;

public record AuthResponse(String token, String tokenType, long expiresIn, String username, String role) {
}

package com.vaishnavi.aegistrace.security;

public enum UserRole {
    ADMIN,
    ANALYST,
    VIEWER;

    public String authority() {
        return "ROLE_" + name();
    }
}

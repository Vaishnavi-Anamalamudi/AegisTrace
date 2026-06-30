package com.vaishnavi.aegistrace.exception;

import java.time.LocalDateTime;

public class ApiError {

    private LocalDateTime timestamp;
    private String message;
    private String details;

    public ApiError(String message, String details) {
        this.timestamp = LocalDateTime.now();
        this.message = message;
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }
}

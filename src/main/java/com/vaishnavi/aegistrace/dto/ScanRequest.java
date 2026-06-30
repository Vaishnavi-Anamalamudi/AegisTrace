package com.vaishnavi.aegistrace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ScanRequest(
        @NotBlank(message = "Scan target is required")
        @Size(max = 253, message = "Scan target must be 253 characters or fewer")
        String target) {
}

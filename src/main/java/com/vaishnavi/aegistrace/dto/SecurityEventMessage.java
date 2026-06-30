package com.vaishnavi.aegistrace.dto;

import java.time.LocalDateTime;

import com.vaishnavi.aegistrace.entity.SecurityEvent;

public record SecurityEventMessage(
        Long id,
        LocalDateTime timestamp,
        String eventType,
        String severity,
        String sourceIP,
        String destinationIP,
        String mitreTactic,
        Double riskScore,
        String description) {

    public static SecurityEventMessage fromEntity(SecurityEvent event) {
        return new SecurityEventMessage(
                event.getId(),
                event.getTimestamp(),
                event.getEventType(),
                event.getSeverity(),
                event.getSourceIP(),
                event.getDestinationIP(),
                event.getMitreTactic(),
                event.getRiskScore(),
                event.getDescription());
    }
}

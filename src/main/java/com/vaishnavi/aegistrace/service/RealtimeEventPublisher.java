package com.vaishnavi.aegistrace.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.vaishnavi.aegistrace.dto.SecurityEventMessage;
import com.vaishnavi.aegistrace.entity.SecurityEvent;

@Service
public class RealtimeEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public RealtimeEventPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void publish(SecurityEvent event) {
        messagingTemplate.convertAndSend("/topic/security-events", SecurityEventMessage.fromEntity(event));
    }
}

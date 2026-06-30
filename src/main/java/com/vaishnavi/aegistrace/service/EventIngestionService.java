package com.vaishnavi.aegistrace.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.vaishnavi.aegistrace.dto.EventRequest;
import com.vaishnavi.aegistrace.entity.EventLog;
import com.vaishnavi.aegistrace.repository.EventRepository;
import com.vaishnavi.aegistrace.entity.SecurityEvent;
import com.vaishnavi.aegistrace.service.SecurityEventService;
import com.vaishnavi.aegistrace.service.MITREMappingService;

@Service
public class EventIngestionService {

    private final EventRepository repository;
    private final SecurityEventService securityEventService;
    private final MITREMappingService mitreMappingService;

    public EventIngestionService(EventRepository repository, SecurityEventService securityEventService, MITREMappingService mitreMappingService) {
        this.repository = repository;
        this.securityEventService = securityEventService;
        this.mitreMappingService = mitreMappingService;
    }

    public EventLog saveEvent(EventRequest request) {

        EventLog event = new EventLog();

        event.setSourceIP(request.getSourceIP());
        event.setDestinationIP(request.getDestinationIP());
        event.setEventType(request.getEventType());
        event.setSeverity(request.getSeverity());
        event.setRawLog(request.getRawLog());
        event.setDescription(request.getDescription());

        event.setTimestamp(LocalDateTime.now());

        event.setRiskScore(0.0);

        return repository.save(event);
    }

    public EventLog ingestEvent(EventRequest request) {
        EventLog saved = saveEvent(request);

        // create a platform SecurityEvent mirror and map MITRE tactic
        try {
            SecurityEvent se = new SecurityEvent();
            se.setTimestamp(saved.getTimestamp());
            se.setSourceIP(saved.getSourceIP());
            se.setDestinationIP(saved.getDestinationIP());
            se.setEventType(saved.getEventType());
            se.setSeverity(saved.getSeverity());
            se.setDescription(saved.getDescription());
            se.setStatus(saved.getSeverity());
            se.setRiskScore(saved.getRiskScore());
            se.setMitreTactic(mitreMappingService.mapToTactic(se));
            se.setRawLog(saved.getRawLog());
            securityEventService.create(se);
        } catch (Exception ex) {
            // non-fatal; log if logging available
        }

        return saved;
    }
}
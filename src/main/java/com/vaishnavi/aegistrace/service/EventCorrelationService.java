package com.vaishnavi.aegistrace.service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.vaishnavi.aegistrace.entity.EventLog;
import com.vaishnavi.aegistrace.repository.EventRepository;

@Service
public class EventCorrelationService {

    private final EventRepository repository;

    public EventCorrelationService(EventRepository repository) {
        this.repository = repository;
    }

    public Map<String, List<EventLog>> correlateEvents() {

        List<EventLog> events = repository.findAll();

        Map<String, List<EventLog>> groupedByIP = new HashMap<>();

        for (EventLog event : events) {
            String key = event.getSourceIP();
            if (key == null || key.isBlank()) {
                key = event.getDestinationIP();
            }
            if (key == null || key.isBlank()) key = "unknown";

            groupedByIP
                .computeIfAbsent(key, k -> new ArrayList<>())
                .add(event);
        }

        return groupedByIP;
    }
}
package com.vaishnavi.aegistrace.controller;

import com.vaishnavi.aegistrace.dto.EventRequest;
import com.vaishnavi.aegistrace.entity.EventLog;
import com.vaishnavi.aegistrace.service.EventIngestionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ingest/events")
public class EventController {

    private final EventIngestionService service;

    public EventController(EventIngestionService service) {
        this.service = service;
    }

    @PostMapping
    public EventLog createEvent(@RequestBody EventRequest request) {
        return service.ingestEvent(request);
    }
}

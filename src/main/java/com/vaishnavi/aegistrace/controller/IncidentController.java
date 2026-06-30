package com.vaishnavi.aegistrace.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vaishnavi.aegistrace.entity.IncidentReport;
import com.vaishnavi.aegistrace.service.IncidentService;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService service;

    public IncidentController(IncidentService service) {
        this.service = service;
    }

    @GetMapping
    public List<IncidentReport> list() {
        return service.getAllIncidents();
    }

    @PostMapping
    public ResponseEntity<IncidentReport> create(@RequestBody IncidentReport report) {
        return ResponseEntity.ok(service.createIncident(report));
    }
}

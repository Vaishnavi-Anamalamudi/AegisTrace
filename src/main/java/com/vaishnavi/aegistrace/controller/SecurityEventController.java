package com.vaishnavi.aegistrace.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vaishnavi.aegistrace.entity.SecurityEvent;
import com.vaishnavi.aegistrace.service.SecurityEventService;

@RestController
@RequestMapping("/api/events")
public class SecurityEventController {

    private final SecurityEventService service;

    public SecurityEventController(SecurityEventService service) {
        this.service = service;
    }

    @GetMapping
    public List<SecurityEvent> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SecurityEvent> get(@PathVariable Long id) {
        SecurityEvent e = service.findById(id);
        return e == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(e);
    }

    @PostMapping
    public ResponseEntity<SecurityEvent> create(@RequestBody SecurityEvent event) {
        SecurityEvent saved = service.create(event);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

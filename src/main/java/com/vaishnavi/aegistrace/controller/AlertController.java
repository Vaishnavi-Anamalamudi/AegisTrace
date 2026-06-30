package com.vaishnavi.aegistrace.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vaishnavi.aegistrace.entity.Alert;
import com.vaishnavi.aegistrace.service.AlertService;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService service;

    public AlertController(AlertService service) {
        this.service = service;
    }

    @GetMapping
    public List<Alert> list() { return service.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Alert> get(@PathVariable Long id) {
        Alert a = service.findById(id);
        return a == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(a);
    }

    @PostMapping
    public ResponseEntity<Alert> create(@RequestBody Alert alert) {
        return ResponseEntity.ok(service.create(alert));
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<Void> resolve(@PathVariable Long id) {
        service.resolve(id);
        return ResponseEntity.noContent().build();
    }
}

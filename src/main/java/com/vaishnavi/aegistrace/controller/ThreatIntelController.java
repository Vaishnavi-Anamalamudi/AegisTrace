package com.vaishnavi.aegistrace.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vaishnavi.aegistrace.entity.ThreatIntel;
import com.vaishnavi.aegistrace.service.ThreatIntelService;

@RestController
@RequestMapping("/api/threats")
public class ThreatIntelController {

    private final ThreatIntelService service;

    public ThreatIntelController(ThreatIntelService service) {
        this.service = service;
    }

    @GetMapping
    public List<ThreatIntel> list() { return service.findAll(); }

    @GetMapping("/ip/{ip}")
    public ResponseEntity<ThreatIntel> byIp(@PathVariable String ip) {
        ThreatIntel t = service.findByIp(ip);
        return t == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(t);
    }

    @PostMapping
    public ResponseEntity<ThreatIntel> create(@RequestBody ThreatIntel t) {
        return ResponseEntity.ok(service.create(t));
    }
}


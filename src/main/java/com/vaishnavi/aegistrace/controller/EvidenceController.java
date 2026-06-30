package com.vaishnavi.aegistrace.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vaishnavi.aegistrace.entity.ForensicsRecord;
import com.vaishnavi.aegistrace.service.ForensicsService;

@RestController
@RequestMapping("/api/evidence")
public class EvidenceController {

    private final ForensicsService service;

    public EvidenceController(ForensicsService service) {
        this.service = service;
    }

    @GetMapping
    public List<ForensicsRecord> list() {
        return service.getAllRecords();
    }

    @PostMapping
    public ResponseEntity<ForensicsRecord> create(@RequestBody ForensicsRecord record) {
        return ResponseEntity.ok(service.createRecord(record));
    }
}

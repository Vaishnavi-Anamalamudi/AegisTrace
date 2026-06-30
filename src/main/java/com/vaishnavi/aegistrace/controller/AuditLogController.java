package com.vaishnavi.aegistrace.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vaishnavi.aegistrace.entity.AuditLog;
import com.vaishnavi.aegistrace.service.AuditLogService;

@RestController
@RequestMapping("/api/audit")
public class AuditLogController {

    private final AuditLogService service;

    public AuditLogController(AuditLogService service) { this.service = service; }

    @GetMapping
    public List<AuditLog> list() { return service.findAll(); }
}

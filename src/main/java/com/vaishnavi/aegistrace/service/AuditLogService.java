package com.vaishnavi.aegistrace.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vaishnavi.aegistrace.entity.AuditLog;
import com.vaishnavi.aegistrace.repository.AuditLogRepository;

@Service
public class AuditLogService {

    private final AuditLogRepository repository;

    public AuditLogService(AuditLogRepository repository) {
        this.repository = repository;
    }

    public AuditLog record(AuditLog log) {
        return repository.save(log);
    }

    public List<AuditLog> findAll() {
        return repository.findAll();
    }
}

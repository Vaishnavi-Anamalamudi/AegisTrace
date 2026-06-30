package com.vaishnavi.aegistrace.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vaishnavi.aegistrace.entity.SecurityEvent;
import com.vaishnavi.aegistrace.repository.SecurityEventRepository;

@Service
public class SecurityEventService {

    private final SecurityEventRepository repository;

    public SecurityEventService(SecurityEventRepository repository) {
        this.repository = repository;
    }

    public SecurityEvent create(SecurityEvent event) {
        return repository.save(event);
    }

    public List<SecurityEvent> findAll() {
        return repository.findAll();
    }

    public List<SecurityEvent> findLatest() {
        return repository.findTop25ByOrderByTimestampDesc();
    }

    public SecurityEvent findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}

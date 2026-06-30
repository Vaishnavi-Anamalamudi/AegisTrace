package com.vaishnavi.aegistrace.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vaishnavi.aegistrace.entity.Alert;
import com.vaishnavi.aegistrace.repository.AlertRepository;

@Service
public class AlertService {

    private final AlertRepository repository;

    public AlertService(AlertRepository repository) {
        this.repository = repository;
    }

    public Alert create(Alert alert) {
        return repository.save(alert);
    }

    public List<Alert> findAll() {
        return repository.findAll();
    }

    public Alert findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void resolve(Long id) {
        repository.findById(id).ifPresent(a -> { a.setResolved(true); repository.save(a); });
    }
}

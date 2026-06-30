package com.vaishnavi.aegistrace.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.vaishnavi.aegistrace.entity.ThreatIntel;
import com.vaishnavi.aegistrace.repository.ThreatIntelRepository;

@Service
public class ThreatIntelService {

    private final ThreatIntelRepository repository;

    public ThreatIntelService(ThreatIntelRepository repository) {
        this.repository = repository;
    }

    public ThreatIntel create(ThreatIntel t) {
        return repository.save(t);
    }

    public List<ThreatIntel> findAll() {
        return repository.findAll();
    }

    public Optional<ThreatIntel> findByIp(String ip) {
        if (ip == null || ip.isBlank()) {
            return Optional.empty();
        }
        return repository.findFirstByIpAddressIgnoreCase(ip.trim());
    }
}

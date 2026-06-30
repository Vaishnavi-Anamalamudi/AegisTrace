package com.vaishnavi.aegistrace.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vaishnavi.aegistrace.entity.IncidentReport;
import com.vaishnavi.aegistrace.repository.IncidentReportRepository;

@Service
public class IncidentService {

    private final IncidentReportRepository repository;

    public IncidentService(IncidentReportRepository repository) {
        this.repository = repository;
    }

    public IncidentReport createIncident(IncidentReport report) {
        return repository.save(report);
    }

    public List<IncidentReport> getAllIncidents() {
        return repository.findAll();
    }
}

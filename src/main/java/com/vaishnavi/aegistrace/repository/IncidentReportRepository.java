package com.vaishnavi.aegistrace.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vaishnavi.aegistrace.entity.IncidentReport;

public interface IncidentReportRepository extends JpaRepository<IncidentReport, Long> {
}

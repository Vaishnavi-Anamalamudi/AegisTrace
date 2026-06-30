package com.vaishnavi.aegistrace.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "scan_results")
public class ScanResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String target;

    private String domain;

    private LocalDateTime timestamp = LocalDateTime.now();

    private String osFingerprint;

    private Double riskScore;

    private String status;

    private Long durationMillis;

    private String scannerUser;

    @Column(length = 5000)
    private String resultSummary;

    @ElementCollection
    @CollectionTable(name = "scan_result_ports", joinColumns = @JoinColumn(name = "scan_result_id"))
    @Column(name = "port")
    private List<String> openPorts;

    @ElementCollection
    @CollectionTable(name = "scan_result_services", joinColumns = @JoinColumn(name = "scan_result_id"))
    @Column(name = "service")
    private List<String> services;

    public Long getId() {
        return id;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getOsFingerprint() {
        return osFingerprint;
    }

    public void setOsFingerprint(String osFingerprint) {
        this.osFingerprint = osFingerprint;
    }

    public Double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Double riskScore) {
        this.riskScore = riskScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getDurationMillis() {
        return durationMillis;
    }

    public void setDurationMillis(Long durationMillis) {
        this.durationMillis = durationMillis;
    }

    public String getScannerUser() {
        return scannerUser;
    }

    public void setScannerUser(String scannerUser) {
        this.scannerUser = scannerUser;
    }

    public String getResultSummary() {
        return resultSummary;
    }

    public void setResultSummary(String resultSummary) {
        this.resultSummary = resultSummary;
    }

    public List<String> getOpenPorts() {
        return openPorts;
    }

    public void setOpenPorts(List<String> openPorts) {
        this.openPorts = openPorts;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }
}

-- AegisTrace baseline schema reference.
-- This mirrors the current JPA model and is intended for documentation until
-- versioned Flyway or Liquibase migrations are introduced.

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    role VARCHAR(32) NOT NULL,
    email VARCHAR(254) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL
);

CREATE TABLE security_events (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    timestamp DATETIME,
    sourceip VARCHAR(255),
    destinationip VARCHAR(255),
    username VARCHAR(255),
    event_type VARCHAR(255),
    severity VARCHAR(255),
    description VARCHAR(5000),
    status VARCHAR(255),
    mitre_tactic VARCHAR(255),
    risk_score DOUBLE,
    raw_log VARCHAR(5000)
);

CREATE TABLE event_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sourceip VARCHAR(255),
    destinationip VARCHAR(255),
    event_type VARCHAR(255),
    description VARCHAR(255),
    severity VARCHAR(255),
    risk_score DOUBLE,
    timestamp DATETIME,
    raw_log VARCHAR(5000)
);

CREATE TABLE alerts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    timestamp DATETIME,
    alert_type VARCHAR(255),
    severity VARCHAR(255),
    sourceip VARCHAR(255),
    destinationip VARCHAR(255),
    detail VARCHAR(4000),
    resolved BOOLEAN
);

CREATE TABLE scan_results (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    target VARCHAR(255),
    domain VARCHAR(255),
    timestamp DATETIME,
    os_fingerprint VARCHAR(255),
    risk_score DOUBLE,
    status VARCHAR(255),
    duration_millis BIGINT,
    scanner_user VARCHAR(255),
    result_summary VARCHAR(5000)
);

CREATE TABLE scan_result_ports (
    scan_result_id BIGINT NOT NULL,
    port VARCHAR(255),
    CONSTRAINT fk_scan_result_ports_scan
        FOREIGN KEY (scan_result_id) REFERENCES scan_results(id)
);

CREATE TABLE scan_result_services (
    scan_result_id BIGINT NOT NULL,
    service VARCHAR(255),
    CONSTRAINT fk_scan_result_services_scan
        FOREIGN KEY (scan_result_id) REFERENCES scan_results(id)
);

CREATE TABLE vulnerabilities (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255),
    severity VARCHAR(255),
    description VARCHAR(255),
    recommendation VARCHAR(255),
    scan_result_id BIGINT,
    CONSTRAINT fk_vulnerabilities_scan
        FOREIGN KEY (scan_result_id) REFERENCES scan_results(id)
);

CREATE TABLE incident_reports (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255),
    created_at DATETIME,
    risk_score DOUBLE,
    summary VARCHAR(4000),
    impact_analysis VARCHAR(4000),
    remediation_steps VARCHAR(4000)
);

CREATE TABLE forensics_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    evidence_id VARCHAR(255),
    source VARCHAR(255),
    timestamp DATETIME,
    severity VARCHAR(255),
    summary VARCHAR(4000),
    chain_of_custody VARCHAR(4000)
);

CREATE TABLE ioc (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(255),
    value VARCHAR(255),
    category VARCHAR(255),
    description VARCHAR(2000)
);

CREATE TABLE threat_intel (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ip_address VARCHAR(255),
    threat_level VARCHAR(255),
    country VARCHAR(255),
    malware_family VARCHAR(255),
    description VARCHAR(4000),
    first_seen DATETIME
);

CREATE TABLE audit_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_time DATETIME,
    actor VARCHAR(255),
    action VARCHAR(255),
    details VARCHAR(4000)
);

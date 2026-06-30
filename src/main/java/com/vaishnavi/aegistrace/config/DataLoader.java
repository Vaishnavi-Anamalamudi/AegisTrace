package com.vaishnavi.aegistrace.config;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.vaishnavi.aegistrace.entity.Alert;
import com.vaishnavi.aegistrace.entity.ForensicsRecord;
import com.vaishnavi.aegistrace.entity.IncidentReport;
import com.vaishnavi.aegistrace.entity.ScanResult;
import com.vaishnavi.aegistrace.entity.SecurityEvent;
import com.vaishnavi.aegistrace.entity.User;
import com.vaishnavi.aegistrace.repository.AlertRepository;
import com.vaishnavi.aegistrace.repository.ForensicsRecordRepository;
import com.vaishnavi.aegistrace.repository.IncidentReportRepository;
import com.vaishnavi.aegistrace.repository.ScanResultRepository;
import com.vaishnavi.aegistrace.repository.SecurityEventRepository;
import com.vaishnavi.aegistrace.repository.UserRepository;
import com.vaishnavi.aegistrace.service.UserService;

@Component
@Profile("!test")
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final UserService userService;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final SecurityEventRepository securityEventRepository;
    private final AlertRepository alertRepository;
    private final IncidentReportRepository incidentReportRepository;
    private final ForensicsRecordRepository forensicsRecordRepository;
    private final ScanResultRepository scanResultRepository;

    public DataLoader(
            UserService userService,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            SecurityEventRepository securityEventRepository,
            AlertRepository alertRepository,
            IncidentReportRepository incidentReportRepository,
            ForensicsRecordRepository forensicsRecordRepository,
            ScanResultRepository scanResultRepository) {

        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.securityEventRepository = securityEventRepository;
        this.alertRepository = alertRepository;
        this.incidentReportRepository = incidentReportRepository;
        this.forensicsRecordRepository = forensicsRecordRepository;
        this.scanResultRepository = scanResultRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        User admin = userRepository.findByUsernameIgnoreCase("admin").orElse(null);
        if (admin == null) {
            admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("ChangeMe123!"));
            log.info("Created the development administrator account");
        } else {
            log.debug("Development administrator account already exists");
        }
        admin.setEmail("admin@aegistrace.local");
        admin.setRole("ROLE_ADMIN");
        admin.setStatus("ACTIVE");
        userService.createUser(admin);

        createDemoUser("analyst", "analyst@aegistrace.local", "ROLE_ANALYST", "Analyst123!");
        createDemoUser("viewer", "viewer@aegistrace.local", "ROLE_VIEWER", "Viewer123!");

        seedProjectDemoData();
    }

    private void createDemoUser(String username, String email, String role, String password) {
        User user = userRepository.findByUsernameIgnoreCase(username).orElse(null);
        if (user == null) {
            user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
        }
        user.setEmail(email);
        user.setRole(role);
        user.setStatus("ACTIVE");
        userService.createUser(user);
    }

    private void seedProjectDemoData() {
        if (securityEventRepository.count() == 0) {
            securityEventRepository.saveAll(List.of(
                    event("AUTH_FAILURE_BURST", "CRITICAL", "203.0.113.42", "10.0.2.15", "Initial Access", 92d, "ESCALATED",
                            "Repeated VPN login failures followed by one successful session for a privileged account."),
                    event("EDR_PROCESS_INJECTION", "HIGH", "10.0.2.15", "10.0.4.21", "Defense Evasion", 84d, "ESCALATED",
                            "Suspicious process injection detected in a signed Windows utility."),
                    event("DATA_STAGING", "HIGH", "10.0.4.21", "10.0.8.30", "Collection", 78d, "OPEN",
                            "Compressed archive created in a shared finance directory outside normal business hours."),
                    event("DNS_TUNNELING", "MEDIUM", "10.0.4.21", "198.51.100.77", "Command and Control", 67d, "OPEN",
                            "High-frequency DNS queries with encoded subdomains observed."),
                    event("PORT_SCAN", "MEDIUM", "198.51.100.24", "10.0.1.10", "Reconnaissance", 54d, "OPEN",
                            "External source probed common administrative ports."),
                    event("MALWARE_HASH_MATCH", "CRITICAL", "10.0.2.15", "10.0.6.9", "Execution", 95d, "ESCALATED",
                            "Downloaded executable matched a known malware hash in the local IOC set.")
            ));
        }

        if (alertRepository.count() == 0) {
            alertRepository.saveAll(List.of(
                    alert("CORRELATED_INTRUSION", "CRITICAL", "203.0.113.42", "Credential abuse and process injection correlated across VPN and EDR telemetry."),
                    alert("POSSIBLE_EXFILTRATION", "HIGH", "10.0.4.21", "Data staging followed by command-and-control DNS pattern."),
                    alert("EXTERNAL_RECON", "MEDIUM", "198.51.100.24", "Repeated service probes detected against the public web segment.")
            ));
        }

        if (incidentReportRepository.count() == 0) {
            IncidentReport incident = new IncidentReport();
            incident.setTitle("VPN credential compromise with staged data collection");
            incident.setRiskScore(91d);
            incident.setSummary("AegisTrace reconstructed a likely intrusion path from VPN authentication failures to endpoint execution, internal movement, and archive staging.");
            incident.setImpactAnalysis("Privileged access could expose finance files, internal host inventory, and operational credentials if not contained quickly.");
            incident.setRemediationSteps("Disable affected accounts, rotate VPN credentials, isolate impacted endpoints, block suspicious DNS destinations, and review file access logs.");
            incidentReportRepository.save(incident);
        }

        if (forensicsRecordRepository.count() == 0) {
            ForensicsRecord record = new ForensicsRecord();
            record.setEvidenceId("EVID-AEGIS-2026-001");
            record.setSource("EDR memory capture");
            record.setSeverity("CRITICAL");
            record.setSummary("Memory artefacts show injected shellcode markers and a network callback from the compromised workstation.");
            record.setChainOfCustody("Collected by analyst, hashed with SHA-256, stored in evidence vault, reviewed by incident lead.");
            forensicsRecordRepository.save(record);
        }

        if (scanResultRepository.count() == 0) {
            ScanResult scan = new ScanResult();
            scan.setTarget("demo.aegistrace.local");
            scan.setDomain("10.0.1.10");
            scan.setTimestamp(LocalDateTime.now().minusHours(2));
            scan.setOsFingerprint("Linux web edge");
            scan.setRiskScore(68d);
            scan.setStatus("COMPLETED");
            scan.setOpenPorts(List.of("22/tcp", "80/tcp", "443/tcp"));
            scan.setServices(List.of("ssh", "http", "https"));
            scanResultRepository.save(scan);
        }
    }

    private SecurityEvent event(String type, String severity, String source, String destination, String tactic, Double risk, String status, String description) {
        SecurityEvent event = new SecurityEvent();
        event.setTimestamp(LocalDateTime.now().minusMinutes(Math.round(risk)));
        event.setEventType(type);
        event.setSeverity(severity);
        event.setSourceIP(source);
        event.setDestinationIP(destination);
        event.setMitreTactic(tactic);
        event.setRiskScore(risk);
        event.setStatus(status);
        event.setDescription(description);
        event.setUsername("analyst-demo");
        event.setRawLog(type + " source=" + source + " destination=" + destination + " risk=" + risk);
        return event;
    }

    private Alert alert(String type, String severity, String source, String detail) {
        Alert alert = new Alert();
        alert.setTimestamp(LocalDateTime.now().minusMinutes(18));
        alert.setAlertType(type);
        alert.setSeverity(severity);
        alert.setSourceIP(source);
        alert.setDestinationIP("10.0.2.15");
        alert.setDetail(detail);
        alert.setResolved(false);
        return alert;
    }
}

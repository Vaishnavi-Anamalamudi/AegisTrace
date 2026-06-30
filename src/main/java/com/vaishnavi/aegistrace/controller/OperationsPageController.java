package com.vaishnavi.aegistrace.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.vaishnavi.aegistrace.service.ForensicsService;
import com.vaishnavi.aegistrace.service.IncidentService;
import com.vaishnavi.aegistrace.service.ScanService;

@Controller
public class OperationsPageController {

    private final IncidentService incidentService;
    private final ForensicsService forensicsService;
    private final ScanService scanService;

    public OperationsPageController(
            IncidentService incidentService,
            ForensicsService forensicsService,
            ScanService scanService) {
        this.incidentService = incidentService;
        this.forensicsService = forensicsService;
        this.scanService = scanService;
    }

    @GetMapping("/incidents")
    public String incidents(Model model) {
        model.addAttribute("incidents", incidentService.getAllIncidents());
        return "incidents";
    }

    @GetMapping("/evidence")
    public String evidence(Model model) {
        model.addAttribute("records", forensicsService.getAllRecords());
        return "evidence";
    }

    @GetMapping("/threat-intel")
    @Transactional(readOnly = true)
    public String threatIntel(Model model) {
        List<ScanHistoryRow> scans = scanService.findAllScans().stream()
                .map(scan -> new ScanHistoryRow(
                        scan.getTimestamp(),
                        scan.getTarget(),
                        scan.getDomain(),
                        scan.getStatus(),
                        joinValues(scan.getOpenPorts()),
                        joinValues(scan.getServices()),
                        scan.getRiskScore()
                ))
                .toList();

        List<FindingRow> findings = scanService.findAllFindings().stream()
                .map(finding -> new FindingRow(
                        finding.getName(),
                        finding.getDescription(),
                        finding.getSeverity(),
                        finding.getScanResult() == null
                                ? "unknown"
                                : finding.getScanResult().getTarget()
                ))
                .toList();

        model.addAttribute("scans", scans);
        model.addAttribute("findings", findings);
        return "threat-intel";
    }

    private String joinValues(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "None detected";
        }
        return String.join(", ", values);
    }

    private record ScanHistoryRow(
            LocalDateTime timestamp,
            String target,
            String domain,
            String status,
            String openPorts,
            String services,
            Double riskScore) {
    }

    private record FindingRow(
            String name,
            String description,
            String severity,
            String target) {
    }
}

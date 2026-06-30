package com.vaishnavi.aegistrace.controller;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.vaishnavi.aegistrace.entity.Alert;
import com.vaishnavi.aegistrace.entity.ForensicsRecord;
import com.vaishnavi.aegistrace.entity.IncidentReport;
import com.vaishnavi.aegistrace.entity.ScanResult;
import com.vaishnavi.aegistrace.entity.SecurityEvent;
import com.vaishnavi.aegistrace.entity.User;
import com.vaishnavi.aegistrace.service.AlertService;
import com.vaishnavi.aegistrace.service.ForensicsService;
import com.vaishnavi.aegistrace.service.IncidentService;
import com.vaishnavi.aegistrace.service.ScanService;
import com.vaishnavi.aegistrace.service.SecurityEventService;
import com.vaishnavi.aegistrace.service.UserService;

@Controller
public class DashboardController {

    private final SecurityEventService securityEventService;
    private final AlertService alertService;
    private final ScanService scanService;
    private final IncidentService incidentService;
    private final ForensicsService forensicsService;
    private final UserService userService;

    public DashboardController(
            SecurityEventService securityEventService,
            AlertService alertService,
            ScanService scanService,
            IncidentService incidentService,
            ForensicsService forensicsService,
            UserService userService) {
        this.securityEventService = securityEventService;
        this.alertService = alertService;
        this.scanService = scanService;
        this.incidentService = incidentService;
        this.forensicsService = forensicsService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    @Transactional(readOnly = true)
    public String dashboard(Model model) {
        List<SecurityEvent> events = securityEventService.findAll().stream()
                .sorted(Comparator.comparing(SecurityEvent::getTimestamp, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
        List<SecurityEvent> latestEvents = events.stream().limit(12).toList();
        List<Alert> alerts = alertService.findAll().stream()
                .sorted(Comparator.comparing(Alert::getTimestamp, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
        List<ScanResult> scans = scanService.findAllScans();
        scans.forEach(scan -> {
            if (scan.getOpenPorts() != null) {
                scan.getOpenPorts().size();
            }
            if (scan.getServices() != null) {
                scan.getServices().size();
            }
        });
        List<IncidentReport> incidents = incidentService.getAllIncidents();
        List<ForensicsRecord> forensics = forensicsService.getAllRecords();
        List<User> users = userService.getAllUsers();

        double averageRisk = events.stream()
                .mapToDouble(e -> e.getRiskScore() != null ? e.getRiskScore().doubleValue() : 0d)
                .average()
                .orElse(0d);
        long criticalEvents = events.stream().filter(e -> "CRITICAL".equalsIgnoreCase(e.getSeverity())).count();
        long highEvents = events.stream().filter(e -> "HIGH".equalsIgnoreCase(e.getSeverity())).count();
        long unresolvedAlerts = alerts.stream().filter(a -> !Boolean.TRUE.equals(a.getResolved())).count();
        long escalatedEvents = events.stream().filter(e -> "ESCALATED".equalsIgnoreCase(e.getStatus())).count();
        Map<String, Long> tacticCounts = events.stream()
                .map(e -> e.getMitreTactic() != null ? e.getMitreTactic() : "Unmapped")
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
        String topSource = events.stream()
                .collect(Collectors.groupingBy(e -> e.getSourceIP() != null ? e.getSourceIP() : "unknown", Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("n/a");

        model.addAttribute("events", events);
        model.addAttribute("latestEvents", latestEvents);
        model.addAttribute("alerts", alerts.stream().limit(6).toList());
        model.addAttribute("totalEvents", events.size());
        model.addAttribute("threatLevel", getThreatLevel(averageRisk));
        model.addAttribute("threatScore", Math.round(averageRisk));
        model.addAttribute("criticalEvents", criticalEvents);
        model.addAttribute("highEvents", highEvents);
        model.addAttribute("unresolvedAlerts", unresolvedAlerts);
        model.addAttribute("escalatedEvents", escalatedEvents);
        model.addAttribute("activeScans", scans.size());
        model.addAttribute("vulnCount", scans.stream().mapToInt(s -> s.getOpenPorts() != null ? s.getOpenPorts().size() : 0).sum());
        model.addAttribute("incidentCount", incidents.size());
        model.addAttribute("forensicsCount", forensics.size());
        model.addAttribute("userCount", users.size());
        model.addAttribute("scanCount", scans.size());
        model.addAttribute("incidentList", incidents.stream().limit(5).toList());
        model.addAttribute("forensicsList", forensics.stream().limit(5).toList());
        model.addAttribute("scanList", scans.stream().limit(5).toList());
        model.addAttribute("userList", users);
        model.addAttribute("correlatedSources", events.stream().map(SecurityEvent::getSourceIP).filter(ip -> ip != null && !ip.isBlank()).distinct().count());
        model.addAttribute("topSource", topSource);
        model.addAttribute("tacticCounts", tacticCounts);

        return "dashboard";
    }

    private String getThreatLevel(double score) {
        if (score >= 80) {
            return "Critical";
        }
        if (score >= 60) {
            return "High";
        }
        if (score >= 30) {
            return "Medium";
        }
        return "Low";
    }
}

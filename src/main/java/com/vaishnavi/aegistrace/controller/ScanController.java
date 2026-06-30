package com.vaishnavi.aegistrace.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vaishnavi.aegistrace.dto.ScanRequest;
import com.vaishnavi.aegistrace.dto.ScanResponse;
import com.vaishnavi.aegistrace.service.ScanService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/scans")
public class ScanController {

    private final ScanService scanService;

    public ScanController(ScanService scanService) {
        this.scanService = scanService;
    }

    @PostMapping("/run")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public ScanResponse runScan(@Valid @RequestBody ScanRequest request, Principal principal) {
        String scannerUser = principal == null ? "system" : principal.getName();
        return ScanResponse.fromEntity(scanService.runTargetScan(request.target(), scannerUser));
    }

    @GetMapping
    public List<ScanResponse> getAllScans(@RequestParam(required = false) String q) {
        return scanService.searchScans(q).stream()
                .map(ScanResponse::fromEntity)
                .toList();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteScan(@PathVariable Long id) {
        scanService.deleteScan(id);
    }

    @GetMapping(value = "/export.csv", produces = "text/csv")
    public org.springframework.http.ResponseEntity<String> exportCsv(@RequestParam(required = false) String q) {
        StringBuilder csv = new StringBuilder("id,target,address,timestamp,status,open_ports,services,duration_ms,scanner_user,risk_score\n");
        scanService.searchScans(q).forEach(scan -> csv.append(scan.getId()).append(',')
                .append(escape(scan.getTarget())).append(',')
                .append(escape(scan.getDomain())).append(',')
                .append(escape(String.valueOf(scan.getTimestamp()))).append(',')
                .append(escape(scan.getStatus())).append(',')
                .append(escape(String.join(" | ", scan.getOpenPorts()))).append(',')
                .append(escape(String.join(" | ", scan.getServices()))).append(',')
                .append(scan.getDurationMillis()).append(',')
                .append(escape(scan.getScannerUser())).append(',')
                .append(scan.getRiskScore()).append('\n'));
        return org.springframework.http.ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=aegistrace-scan-history.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv.toString());
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}

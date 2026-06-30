package com.vaishnavi.aegistrace.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.vaishnavi.aegistrace.entity.ScanResult;

public record ScanResponse(
        Long id,
        String target,
        String domain,
        LocalDateTime timestamp,
        String status,
        List<String> openPorts,
        List<String> services,
        String osFingerprint,
        Long durationMillis,
        String scannerUser,
        Double riskScore,
        String resultSummary) {

    public static ScanResponse fromEntity(ScanResult scan) {
        return new ScanResponse(
                scan.getId(),
                scan.getTarget(),
                scan.getDomain(),
                scan.getTimestamp(),
                scan.getStatus(),
                scan.getOpenPorts(),
                scan.getServices(),
                scan.getOsFingerprint(),
                scan.getDurationMillis(),
                scan.getScannerUser(),
                scan.getRiskScore(),
                scan.getResultSummary());
    }
}

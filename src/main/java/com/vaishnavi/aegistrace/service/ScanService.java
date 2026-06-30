package com.vaishnavi.aegistrace.service;

import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.vaishnavi.aegistrace.entity.Alert;
import com.vaishnavi.aegistrace.entity.ScanResult;
import com.vaishnavi.aegistrace.entity.SecurityEvent;
import com.vaishnavi.aegistrace.entity.VulnerabilityFinding;
import com.vaishnavi.aegistrace.repository.AlertRepository;
import com.vaishnavi.aegistrace.repository.ScanResultRepository;
import com.vaishnavi.aegistrace.repository.SecurityEventRepository;
import com.vaishnavi.aegistrace.repository.VulnerabilityRepository;

@Service
public class ScanService {

    private static final Logger log = LoggerFactory.getLogger(ScanService.class);

    private static final int CONNECT_TIMEOUT_MS = 100;

    private static final Map<Integer, String> COMMON_PORTS = commonPorts();

    private final ScanResultRepository scanResultRepository;
    private final VulnerabilityRepository vulnerabilityRepository;
    private final SecurityEventRepository securityEventRepository;
    private final AlertRepository alertRepository;
    private final RealtimeEventPublisher realtimeEventPublisher;
    private final boolean nmapEnabled;

    public ScanService(
            ScanResultRepository scanResultRepository,
            VulnerabilityRepository vulnerabilityRepository,
            SecurityEventRepository securityEventRepository,
            AlertRepository alertRepository,
            RealtimeEventPublisher realtimeEventPublisher,
            @Value("${aegistrace.scanner.nmap.enabled:true}") boolean nmapEnabled) {

        this.scanResultRepository = scanResultRepository;
        this.vulnerabilityRepository = vulnerabilityRepository;
        this.securityEventRepository = securityEventRepository;
        this.alertRepository = alertRepository;
        this.realtimeEventPublisher = realtimeEventPublisher;
        this.nmapEnabled = nmapEnabled;
    }

    @Transactional
    public ScanResult runTargetScan(String target, String scannerUsername) {
        long started = System.nanoTime();

        String normalizedTarget = normalizeTarget(target);

        InetAddress resolvedAddress = resolve(normalizedTarget);

        ProbeResult probeResult = resolvedAddress == null
                ? ProbeResult.unresolved()
                : scanWithBestAvailableEngine(normalizedTarget, resolvedAddress.getHostAddress());

        ScanResult scan = new ScanResult();

        scan.setTarget(normalizedTarget);
        scan.setDomain(buildDomainLabel(resolvedAddress));
        scan.setTimestamp(LocalDateTime.now());
        scan.setStatus(resolvedAddress == null ? "FAILED_DNS" : "COMPLETED");
        scan.setOsFingerprint(probeResult.osFingerprint());
        scan.setOpenPorts(probeResult.openPorts());
        scan.setServices(probeResult.services());
        scan.setRiskScore(calculateRisk(probeResult.openPorts()));
        scan.setDurationMillis(Duration.ofNanos(System.nanoTime() - started).toMillis());
        scan.setScannerUser(scannerUsername);
        scan.setResultSummary(probeResult.summary());

        ScanResult savedScan = scanResultRepository.save(scan);

        saveExposureFindings(savedScan, probeResult.openPorts());
        SecurityEvent scanEvent = saveScanEvent(savedScan);
        createAlertsForScan(savedScan);

        realtimeEventPublisher.publish(scanEvent);
        log.info("Scan completed target={} user={} status={} openPorts={} durationMs={}",
                savedScan.getTarget(),
                scannerUsername,
                savedScan.getStatus(),
                savedScan.getOpenPorts().size(),
                savedScan.getDurationMillis());

        return savedScan;
    }

    @Transactional(readOnly = true)
    public List<ScanResult> findAllScans() {
        List<ScanResult> scans = scanResultRepository.findAllByOrderByTimestampDesc();
        scans.forEach(this::initializeScanCollections);
        return scans;
    }

    @Transactional(readOnly = true)
    public List<ScanResult> searchScans(String query) {
        List<ScanResult> scans = !hasText(query)
                ? scanResultRepository.findAllByOrderByTimestampDesc()
                : scanResultRepository.findByTargetContainingIgnoreCaseOrDomainContainingIgnoreCaseOrderByTimestampDesc(query, query);
        scans.forEach(this::initializeScanCollections);
        return scans;
    }

    @Transactional
    public void deleteScan(Long id) {
        scanResultRepository.deleteById(id);
        log.info("Deleted scan result id={}", id);
    }

    public List<VulnerabilityFinding> findAllFindings() {
        return vulnerabilityRepository.findAllByOrderByIdDesc();
    }

    private static Map<Integer, String> commonPorts() {

        Map<Integer, String> ports = new LinkedHashMap<>();

        ports.put(20, "ftp-data");
        ports.put(21, "ftp");
        ports.put(22, "ssh");
        ports.put(23, "telnet");
        ports.put(25, "smtp");
        ports.put(53, "dns");
        ports.put(80, "http");
        ports.put(110, "pop3");
        ports.put(143, "imap");
        ports.put(443, "https");
        ports.put(445, "smb");
        ports.put(587, "smtp-submission");
        ports.put(993, "imaps");
        ports.put(995, "pop3s");
        ports.put(1433, "mssql");
        ports.put(1521, "oracle");
        ports.put(3306, "mysql");
        ports.put(3389, "rdp");
        ports.put(5432, "postgresql");
        ports.put(6379, "redis");
        ports.put(8080, "http-alt");
        ports.put(8443, "https-alt");
        ports.put(9200, "elasticsearch");

        return ports;
    }

    private ProbeResult scanWithBestAvailableEngine(String normalizedTarget, String hostIp) {
        if (nmapEnabled) {
            try {
                return scanWithNmap(normalizedTarget);
            } catch (IOException ex) {
                log.warn("Nmap unavailable for target={}, falling back to TCP probe: {}", normalizedTarget, ex.getMessage());
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                log.warn("Nmap interrupted for target={}, falling back to TCP probe", normalizedTarget);
            } catch (RuntimeException ex) {
                log.warn("Nmap parse failed for target={}, falling back to TCP probe: {}", normalizedTarget, ex.getMessage());
            }
        }
        return scanCommonTcpPorts(hostIp);
    }

    private ProbeResult scanWithNmap(String target) throws IOException, InterruptedException {
        Process process = new ProcessBuilder(
                "nmap",
                "-sV",
                "-O",
                "--open",
                "-oX",
                "-",
                target)
                .redirectErrorStream(true)
                .start();

        boolean finished = process.waitFor(45, TimeUnit.SECONDS);
        String output = new String(process.getInputStream().readAllBytes());
        if (!finished) {
            process.destroyForcibly();
            throw new IOException("nmap timed out");
        }
        if (process.exitValue() != 0) {
            throw new IOException("nmap exited with code " + process.exitValue());
        }
        return parseNmapXml(output);
    }

    private ProbeResult parseNmapXml(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            Document document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
            NodeList portNodes = document.getElementsByTagName("port");
            List<String> openPorts = new ArrayList<>();
            List<String> services = new ArrayList<>();

            for (int index = 0; index < portNodes.getLength(); index += 1) {
                var portNode = portNodes.item(index);
                var attributes = portNode.getAttributes();
                String protocol = attributes.getNamedItem("protocol").getNodeValue();
                String portId = attributes.getNamedItem("portid").getNodeValue();
                openPorts.add(portId + "/" + protocol);

                NodeList children = portNode.getChildNodes();
                for (int childIndex = 0; childIndex < children.getLength(); childIndex += 1) {
                    var child = children.item(childIndex);
                    if ("service".equals(child.getNodeName()) && child.hasAttributes()) {
                        var serviceAttrs = child.getAttributes();
                        String name = serviceAttrs.getNamedItem("name") == null
                                ? "unknown"
                                : serviceAttrs.getNamedItem("name").getNodeValue();
                        String product = serviceAttrs.getNamedItem("product") == null
                                ? ""
                                : " " + serviceAttrs.getNamedItem("product").getNodeValue();
                        String version = serviceAttrs.getNamedItem("version") == null
                                ? ""
                                : " " + serviceAttrs.getNamedItem("version").getNodeValue();
                        services.add((name + product + version).trim());
                    }
                }
            }

            String os = "Nmap service/version scan";
            NodeList osMatches = document.getElementsByTagName("osmatch");
            if (osMatches.getLength() > 0 && osMatches.item(0).hasAttributes()) {
                os = osMatches.item(0).getAttributes().getNamedItem("name").getNodeValue();
            }
            return new ProbeResult(openPorts, services, os, "Nmap scan completed with service/version detection.");
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to parse nmap XML", ex);
        }
    }

    private ProbeResult scanCommonTcpPorts(String hostIp) {
        List<String> openPorts = new ArrayList<>();
        List<String> services = new ArrayList<>();

        for (Integer port : COMMON_PORTS.keySet()) {
            if (isPortOpen(hostIp, port)) {
                openPorts.add(port + "/tcp");
                services.add(COMMON_PORTS.getOrDefault(port, "unknown"));
            }
        }
        return new ProbeResult(openPorts, services, "TCP Common Port Scan", "Nmap was unavailable; completed common TCP probe.");
    }

    private String normalizeTarget(String target) {

        if (!hasText(target)) {
            throw new IllegalArgumentException(
                    "Scan target is required"
            );
        }

        String normalized = target
                .trim()
                .toLowerCase(Locale.ROOT)
                .replace("https://", "")
                .replace("http://", "")
                .split("/", 2)[0];

        if (!normalized.matches("^[a-z0-9][a-z0-9._:-]{0,252}$")) {
            throw new IllegalArgumentException("Scan target must be a valid IP address, hostname, or domain");
        }

        return normalized;
    }

    private InetAddress resolve(String target) {

        try {
            return InetAddress.getByName(target);
        } catch (UnknownHostException ex) {
            return null;
        }
    }

    private boolean isPortOpen(String hostAddress, int port) {

        try (Socket socket = new Socket()) {

            socket.connect(
                    new InetSocketAddress(hostAddress, port),
                    CONNECT_TIMEOUT_MS
            );

            return true;

        } catch (IOException ex) {

            return false;
        }
    }

    private String buildDomainLabel(InetAddress resolvedAddress) {
        if (resolvedAddress == null) {
            return "unresolved";
        }

        return resolvedAddress.getHostAddress();
    }

    private double calculateRisk(List<String> openPorts) {

        double risk = openPorts.size() * 6.0;

        if (openPorts.contains("23/tcp")) {
            risk += 28.0;
        }

        if (openPorts.contains("445/tcp")
                || openPorts.contains("3389/tcp")) {

            risk += 22.0;
        }

        if (openPorts.contains("3306/tcp")
                || openPorts.contains("5432/tcp")
                || openPorts.contains("6379/tcp")) {

            risk += 18.0;
        }

        return Math.min(100.0, risk);
    }

    private void saveExposureFindings(
            ScanResult scan,
            List<String> openPorts) {

        for (String openPort : openPorts) {

            VulnerabilityFinding finding =
                    new VulnerabilityFinding();

            finding.setName("Open " + openPort);

            finding.setSeverity(
                    severityFor(openPort)
            );

            finding.setDescription(
                    "The TCP service "
                            + openPort
                            + " responded from this network location."
            );

            finding.setRecommendation(
                    "Verify this exposure is intentional, patched, and restricted to trusted sources."
            );

            finding.setScanResult(scan);

            vulnerabilityRepository.save(finding);
        }
    }

    private SecurityEvent saveScanEvent(ScanResult scan) {
        SecurityEvent event = new SecurityEvent();
        event.setTimestamp(LocalDateTime.now());
        event.setSourceIP(scan.getDomain());
        event.setDestinationIP(scan.getTarget());
        event.setUsername(scan.getScannerUser());
        event.setEventType("PORT_SCAN_COMPLETED");
        event.setSeverity(scan.getRiskScore() >= 75 ? "HIGH" : "MEDIUM");
        event.setStatus(scan.getRiskScore() >= 75 ? "ESCALATED" : "OPEN");
        event.setMitreTactic("Reconnaissance");
        event.setRiskScore(scan.getRiskScore());
        event.setDescription("Exposure scan completed for " + scan.getTarget()
                + " with " + scan.getOpenPorts().size() + " open TCP service(s).");
        event.setRawLog(scan.getResultSummary());
        return securityEventRepository.save(event);
    }

    private void createAlertsForScan(ScanResult scan) {
        for (String openPort : scan.getOpenPorts()) {
            if (isAlertPort(openPort)) {
                Alert alert = new Alert();
                alert.setTimestamp(LocalDateTime.now());
                alert.setAlertType(alertTypeFor(openPort));
                alert.setSeverity(severityFor(openPort).toUpperCase(Locale.ROOT));
                alert.setSourceIP(scan.getDomain());
                alert.setDestinationIP(scan.getTarget());
                alert.setDetail("Scan " + scan.getId() + " detected exposed " + openPort
                        + " on " + scan.getTarget() + ". Review network ACLs and ownership.");
                alert.setResolved(false);
                alertRepository.save(alert);
            }
        }
    }

    private boolean isAlertPort(String openPort) {
        return "22/tcp".equals(openPort)
                || "23/tcp".equals(openPort)
                || "445/tcp".equals(openPort)
                || "3389/tcp".equals(openPort)
                || "3306/tcp".equals(openPort)
                || "5432/tcp".equals(openPort)
                || "6379/tcp".equals(openPort);
    }

    private String alertTypeFor(String openPort) {
        return switch (openPort) {
            case "22/tcp" -> "SSH_EXPOSED";
            case "3389/tcp" -> "RDP_EXPOSED";
            case "23/tcp" -> "TELNET_EXPOSED";
            case "445/tcp" -> "SMB_EXPOSED";
            default -> "HIGH_RISK_SERVICE_EXPOSED";
        };
    }

    private String severityFor(String openPort) {

        if ("23/tcp".equals(openPort)
                || "445/tcp".equals(openPort)
                || "3389/tcp".equals(openPort)) {

            return "High";
        }

        if ("3306/tcp".equals(openPort)
                || "5432/tcp".equals(openPort)
                || "6379/tcp".equals(openPort)) {

            return "Medium";
        }

        return "Low";
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private void initializeScanCollections(ScanResult scan) {
        if (scan.getOpenPorts() != null) {
            scan.getOpenPorts().size();
        }
        if (scan.getServices() != null) {
            scan.getServices().size();
        }
    }

    private record ProbeResult(
            List<String> openPorts,
            List<String> services,
            String osFingerprint,
            String summary) {

        private static ProbeResult unresolved() {
            return new ProbeResult(
                    List.of(),
                    List.of(),
                    "Host could not be resolved",
                    "DNS resolution failed before scanning.");
        }
    }
}

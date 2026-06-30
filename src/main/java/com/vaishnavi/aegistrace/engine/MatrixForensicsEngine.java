package com.vaishnavi.aegistrace.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.vaishnavi.aegistrace.entity.AttackGraph;
import com.vaishnavi.aegistrace.entity.EventLog;
import com.vaishnavi.aegistrace.service.AttackChainService;
import com.vaishnavi.aegistrace.service.AttackGraphService;
import com.vaishnavi.aegistrace.service.EventCorrelationService;
import com.vaishnavi.aegistrace.service.RiskScoringService;

@Service
public class MatrixForensicsEngine {

    private final EventCorrelationService correlationService;
    private final AttackChainService chainService;
    private final AttackGraphService graphService;
    private final RiskScoringService riskService;

    public MatrixForensicsEngine(
            EventCorrelationService correlationService,
            AttackChainService chainService,
            AttackGraphService graphService,
            RiskScoringService riskService
    ) {
        this.correlationService = correlationService;
        this.chainService = chainService;
        this.graphService = graphService;
        this.riskService = riskService;
    }

    public Map<String, Object> reconstructAttack(Long id) {

        Map<String, Object> forensicReport = new HashMap<>();
        forensicReport.put("attackId", id);

        // 1. Correlate events (group by IP)
        Map<String, List<EventLog>> correlated = correlationService.correlateEvents();
        forensicReport.put("correlatedEvents", correlated);

        // 2. Build attack chains
        List<String> allChains = new ArrayList<>();

        correlated.values().forEach(events -> {
            allChains.addAll(chainService.buildAttackChain(events));
        });

        forensicReport.put("attackChains", allChains);

        // 3. Build attack graph
        List<EventLog> allEvents = correlated.values()
                .stream()
                .flatMap(List::stream)
                .toList();

        AttackGraph graph = graphService.buildGraph(allEvents);
        forensicReport.put("attackGraph", graph);

        // 4. Calculate risk score
        double totalRisk = allEvents.stream()
                .mapToDouble(riskService::calculateRisk)
                .average()
                .orElse(0.0);

        forensicReport.put("riskScore", totalRisk);

        // 5. Final summary (AI-style output)
        forensicReport.put("summary",
                generateSummary(totalRisk, allEvents.size())
        );

        return forensicReport;
    }

    private String generateSummary(double risk, int events) {

        if (risk > 70) {
            return "HIGH SEVERITY ATTACK DETECTED: Active intrusion pattern identified across " + events + " event(s).";
        } else if (risk > 40) {
            return "MEDIUM RISK: Suspicious activity detected in " + events + " event(s), possible reconnaissance phase.";
        } else {
            return "LOW RISK: Normal system behavior observed across " + events + " event(s).";
        }
    }
}
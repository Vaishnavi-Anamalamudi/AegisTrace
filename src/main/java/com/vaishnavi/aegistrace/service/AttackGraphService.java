package com.vaishnavi.aegistrace.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vaishnavi.aegistrace.entity.AttackGraph;
import com.vaishnavi.aegistrace.entity.AttackNode;
import com.vaishnavi.aegistrace.entity.EventLog;

@Service
public class AttackGraphService {

    public AttackGraph buildGraph(List<EventLog> events) {

        AttackGraph graph = new AttackGraph();

        AttackNode previous = null;

        for (EventLog e : events) {

            AttackNode node = new AttackNode(
                    String.valueOf(e.getId()),
                    e.getEventType(),
                    e.getSourceIP(),
                    e.getRiskScore()
            );

            graph.addNode(node);

            if (previous != null) {
                graph.addEdge(previous.getNodeId(), node.getNodeId());
            }

            previous = node;
        }

        return graph;
    }
}
package com.vaishnavi.aegistrace.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttackGraph {

    private final List<AttackNode> nodes = new ArrayList<>();
    private final Map<String, List<String>> edges = new HashMap<>();

    public void addNode(AttackNode node) {
        nodes.add(node);
    }

    public void addEdge(String from, String to) {
        edges.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
    }

    public List<AttackNode> getNodes() {
        return nodes;
    }

    public Map<String, List<String>> getEdges() {
        return edges;
    }
}

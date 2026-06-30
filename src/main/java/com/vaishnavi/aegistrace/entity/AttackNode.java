package com.vaishnavi.aegistrace.entity;

public class AttackNode {
    private final String nodeId;
    private final String eventType;
    private final String ip;
    private final double weight;

    public AttackNode(String nodeId, String eventType, String ip, double weight) {
        this.nodeId = nodeId;
        this.eventType = eventType;
        this.ip = ip;
        this.weight = weight;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getIp() {
        return ip;
    }

    public double getWeight() {
        return weight;
    }
}
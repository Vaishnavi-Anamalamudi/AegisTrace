package com.vaishnavi.aegistrace.service;

import org.springframework.stereotype.Service;

import com.vaishnavi.aegistrace.entity.EventLog;

@Service
public class RiskScoringService {

    public double calculateRisk(EventLog event) {
        String t = event == null || event.getEventType() == null ? "" : event.getEventType();
        switch (t) {
            case "PORT_SCAN":
                return 40;
            case "SSH_BRUTE_FORCE":
                return 70;
            case "LOGIN_SUCCESS":
                return 30;
            case "FILE_ACCESS":
                return 90;
            default:
                return 10;
        }
    }
}
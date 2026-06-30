package com.vaishnavi.aegistrace.service;

import org.springframework.stereotype.Service;

import com.vaishnavi.aegistrace.entity.SecurityEvent;

@Service
public class MITREMappingService {

    public String mapToTactic(SecurityEvent e) {
        String et = e.getEventType() == null ? "" : e.getEventType().toUpperCase();
        if (et.contains("SCAN") || et.contains("PROBE")) return "Reconnaissance";
        if (et.contains("BRUTE") || et.contains("AUTH_FAIL") || et.contains("LOGIN")) return "Initial Access";
        if (et.contains("PRIV") || et.contains("ESCAL")) return "Privilege Escalation";
        if (et.contains("PERSIST") ) return "Persistence";
        if (et.contains("EXFIL") || et.contains("DATA")) return "Exfiltration";
        return "Execution";
    }
}

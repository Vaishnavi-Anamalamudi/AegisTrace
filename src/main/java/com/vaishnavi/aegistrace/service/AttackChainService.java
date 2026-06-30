package com.vaishnavi.aegistrace.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.vaishnavi.aegistrace.entity.EventLog;

@Service
public class AttackChainService {

    public List<String> buildAttackChain(List<EventLog> events) {

        // Sort events by time (critical for reconstruction). Handle null timestamps.
        events.sort(Comparator.comparing(e -> e.getTimestamp() == null ? java.time.LocalDateTime.MIN : e.getTimestamp()));

        List<String> chain = new ArrayList<>();

        for (EventLog e : events) {

            chain.add(
                e.getTimestamp() + " → " +
                e.getSourceIP() + " → " +
                e.getEventType()
            );
        }

        return chain;
    }
}
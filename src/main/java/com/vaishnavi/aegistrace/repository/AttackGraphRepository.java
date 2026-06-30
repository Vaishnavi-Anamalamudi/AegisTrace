package com.vaishnavi.aegistrace.repository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.vaishnavi.aegistrace.entity.AttackGraph;

@Repository
public class AttackGraphRepository {

    private final Map<Long, AttackGraph> storage = new HashMap<>();

    public void save(Long id, AttackGraph graph) {
        storage.put(id, graph);
    }

    public AttackGraph get(Long id) {
        return storage.get(id);
    }
}

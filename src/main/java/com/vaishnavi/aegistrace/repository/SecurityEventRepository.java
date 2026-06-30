package com.vaishnavi.aegistrace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vaishnavi.aegistrace.entity.SecurityEvent;

@Repository
public interface SecurityEventRepository extends JpaRepository<SecurityEvent, Long> {
    java.util.List<SecurityEvent> findTop25ByOrderByTimestampDesc();
}

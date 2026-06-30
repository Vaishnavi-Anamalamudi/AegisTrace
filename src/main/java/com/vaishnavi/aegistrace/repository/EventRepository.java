package com.vaishnavi.aegistrace.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.vaishnavi.aegistrace.entity.EventLog;

public interface EventRepository extends JpaRepository<EventLog, Long> {
}
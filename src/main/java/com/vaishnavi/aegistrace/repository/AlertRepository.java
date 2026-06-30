package com.vaishnavi.aegistrace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vaishnavi.aegistrace.entity.Alert;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
}

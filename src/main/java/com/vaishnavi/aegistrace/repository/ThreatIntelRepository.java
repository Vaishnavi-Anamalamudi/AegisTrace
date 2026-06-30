package com.vaishnavi.aegistrace.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vaishnavi.aegistrace.entity.ThreatIntel;

@Repository
public interface ThreatIntelRepository extends JpaRepository<ThreatIntel, Long> {
    Optional<ThreatIntel> findFirstByIpAddressIgnoreCase(String ipAddress);
}

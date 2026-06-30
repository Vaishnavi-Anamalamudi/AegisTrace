package com.vaishnavi.aegistrace.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vaishnavi.aegistrace.entity.ScanResult;

public interface ScanResultRepository extends JpaRepository<ScanResult, Long> {
    java.util.List<ScanResult> findAllByOrderByTimestampDesc();

    java.util.List<ScanResult> findByTargetContainingIgnoreCaseOrDomainContainingIgnoreCaseOrderByTimestampDesc(String target, String domain);
}

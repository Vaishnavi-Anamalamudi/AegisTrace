package com.vaishnavi.aegistrace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vaishnavi.aegistrace.entity.IOC;

@Repository
public interface IOCRepository extends JpaRepository<IOC, Long> {
}

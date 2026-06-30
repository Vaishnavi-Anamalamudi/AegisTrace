package com.vaishnavi.aegistrace.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vaishnavi.aegistrace.entity.ForensicsRecord;
import com.vaishnavi.aegistrace.repository.ForensicsRecordRepository;

@Service
public class ForensicsService {

    private final ForensicsRecordRepository repository;

    public ForensicsService(ForensicsRecordRepository repository) {
        this.repository = repository;
    }

    public ForensicsRecord createRecord(ForensicsRecord record) {
        return repository.save(record);
    }

    public List<ForensicsRecord> getAllRecords() {
        return repository.findAll();
    }
}

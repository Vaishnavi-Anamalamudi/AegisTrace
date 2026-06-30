package com.vaishnavi.aegistrace.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vaishnavi.aegistrace.entity.IOC;
import com.vaishnavi.aegistrace.repository.IOCRepository;

@Service
public class IOCService {

    private final IOCRepository repository;

    public IOCService(IOCRepository repository) {
        this.repository = repository;
    }

    public IOC create(IOC ioc) {
        return repository.save(ioc);
    }

    public List<IOC> findAll() {
        return repository.findAll();
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}

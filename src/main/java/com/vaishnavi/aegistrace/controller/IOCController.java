package com.vaishnavi.aegistrace.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vaishnavi.aegistrace.entity.IOC;
import com.vaishnavi.aegistrace.service.IOCService;

@RestController
@RequestMapping("/api/iocs")
public class IOCController {

    private final IOCService service;

    public IOCController(IOCService service) { this.service = service; }

    @GetMapping
    public List<IOC> list() { return service.findAll(); }

    @PostMapping
    public ResponseEntity<IOC> create(@RequestBody IOC ioc) {
        return ResponseEntity.ok(service.create(ioc));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}


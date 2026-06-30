package com.vaishnavi.aegistrace.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vaishnavi.aegistrace.engine.MatrixForensicsEngine;

@RestController
@RequestMapping("/api/forensics")
public class ForensicsController {

    private final MatrixForensicsEngine engine;

    public ForensicsController(MatrixForensicsEngine engine) {
        this.engine = engine;
    }

    @GetMapping("/{id}")
    public Object analyze(@PathVariable Long id) {
        return engine.reconstructAttack(id);
    }
}

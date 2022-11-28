package com.example.controllers;

import com.example.services.NodesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NodesRestController {
    NodesService nodesService;
    NodesRestController(NodesService nodesService){
        this.nodesService= nodesService;
    }

    @GetMapping ("/node/{id}")
    public ResponseEntity<?> getNode (@PathVariable String id) {
        return ResponseEntity
                .badRequest()
                .body("Невалидная схема документа или входные данные не верны.");
    }
}

package com.example.controllers;

import com.example.model.SystemItem;
import com.example.services.NodesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RestController
public class NodesRestController {
    NodesService nodesService;
    NodesRestController(NodesService nodesService){
        this.nodesService= nodesService;
    }

    @GetMapping ("/node/{id}")
    public ResponseEntity<?> getNode (@PathVariable String id){
        SystemItem answer = nodesService.getFolder(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(answer);
    }
}

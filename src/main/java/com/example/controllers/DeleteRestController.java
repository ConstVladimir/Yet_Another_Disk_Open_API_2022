package com.example.controllers;

import com.example.services.DeleteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeleteRestController {
    DeleteService deleteService;
    DeleteRestController(DeleteService deleteService){
        this.deleteService= deleteService;
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> getNode (@PathVariable String id, @RequestParam String date) {
        boolean status = deleteService.deleteItem(id, date);
        if (status) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Удаление прошло успешно.");
        }
        return ResponseEntity
                .badRequest()
                .body("Невалидная схема документа или входные данные не верны.");

    }
}

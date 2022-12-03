package com.example.controllers;

import com.example.services.DeleteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

@RestController
public class DeleteRestController {
    DeleteService deleteService;
    DeleteRestController(DeleteService deleteService){
        this.deleteService= deleteService;
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> getNode (@PathVariable String id, @RequestParam String date) {
        OffsetDateTime datePars;
        try {
            datePars = ZonedDateTime.parse(date,ISO_DATE_TIME).toOffsetDateTime();
        }
        catch (Exception e){
            return ResponseEntity
                    .badRequest()
                    .body("Error DateTime specifying");
        }
        boolean status = deleteService.deleteItem(id, datePars);
        if (status) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Successful deletion");
        }
        return ResponseEntity
                .badRequest()
                .body("Validation Failed");

    }
}

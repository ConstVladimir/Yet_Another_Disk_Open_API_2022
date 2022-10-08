package com.example.controllers;

import com.example.exceptions.SystemItemImportException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionImportRestController {
    @ExceptionHandler(SystemItemImportException.class)
    public ResponseEntity<?> exceptionSystemItemImportHandler (SystemItemImportException exception){
        return ResponseEntity
                .badRequest()
                .body(exception.getMessage());
    }


}

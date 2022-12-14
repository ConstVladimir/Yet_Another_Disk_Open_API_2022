package com.example.controllers.exception;

import com.example.exceptions.SystemItemDeleteException;
import com.example.exceptions.SystemItemImportException;
import com.example.exceptions.SystemItemNodesException;
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
    @ExceptionHandler(SystemItemNodesException.class)
    public ResponseEntity<?> exceptionSystemItemNodesHandler (SystemItemNodesException exception){
        return  ResponseEntity
                .badRequest()
                .body(exception.getMessage());
    }

    @ExceptionHandler(SystemItemDeleteException.class)
    public ResponseEntity<?> exceptionSystemItemNodesHandler (SystemItemDeleteException exception){
        return  ResponseEntity
                .badRequest()
                .body(exception.getMessage());
    }

}

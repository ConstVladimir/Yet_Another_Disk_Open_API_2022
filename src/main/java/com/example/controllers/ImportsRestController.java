package com.example.controllers;

import com.example.model.SystemItemImportRequest;

import com.example.services.ImportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

@RestController
public class ImportsRestController  {

    private ImportService importService;
    //private static Logger logger = (Logger) java.util.logging.Logger.getLogger(ImportsRestController.class.getName());

    public  ImportsRestController (ImportService importService){this.importService = importService;}

    @PostMapping("/imports")
    public ResponseEntity<?> importItems(@RequestBody @Valid SystemItemImportRequest systemItemImportRequest, Errors errors){

        if (errors.hasErrors()){
            return ResponseEntity
                    .badRequest()
                    .body("Validation Failed");
        }
        boolean status = importService.insertItems(systemItemImportRequest);
        if (status){
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Successfully");
        }
        return ResponseEntity
                .badRequest()
                .body("Validation Failed");
    }
}

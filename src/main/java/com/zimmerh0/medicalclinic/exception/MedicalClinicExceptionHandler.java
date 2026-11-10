package com.zimmerh0.medicalclinic.exception;

import com.zimmerh0.medicalclinic.dto.ErrorMessageDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MedicalClinicExceptionHandler {

    @ExceptionHandler(MedicalClinicException.class)
    public ResponseEntity<ErrorMessageDto> handle(MedicalClinicException exception) {
        return ResponseEntity.status(exception.getStatus()).body(new ErrorMessageDto(exception.getMessage()));
    }
}
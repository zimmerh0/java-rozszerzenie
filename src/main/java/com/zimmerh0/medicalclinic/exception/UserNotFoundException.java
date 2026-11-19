package com.zimmerh0.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends MedicalClinicException {

    public UserNotFoundException(Long id) {
        super("User not found: " + id, HttpStatus.NOT_FOUND);
    }
}
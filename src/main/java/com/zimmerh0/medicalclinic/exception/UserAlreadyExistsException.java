package com.zimmerh0.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends MedicalClinicException {

    public UserAlreadyExistsException(String email) {
        super("User already exists: " + email, HttpStatus.CONFLICT);
    }
}
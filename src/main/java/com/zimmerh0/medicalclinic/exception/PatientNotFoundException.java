package com.zimmerh0.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class PatientNotFoundException extends MedicalClinicException {
    public PatientNotFoundException(String email) {
        super("Patient not found: " + email, HttpStatus.NOT_FOUND);
    }
}
package com.zimmerh0.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class PatientAlreadyExistsException extends MedicalClinicException {
    public PatientAlreadyExistsException(String email) {
        super("Patient already exists: " + email, HttpStatus.CONFLICT);
    }
}

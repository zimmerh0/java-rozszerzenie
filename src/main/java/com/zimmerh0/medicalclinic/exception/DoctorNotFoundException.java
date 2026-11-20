package com.zimmerh0.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class DoctorNotFoundException extends MedicalClinicException {
    public DoctorNotFoundException(Long id) {
        super("Doctor not found: "+ id, HttpStatus.NOT_FOUND);
    }
}

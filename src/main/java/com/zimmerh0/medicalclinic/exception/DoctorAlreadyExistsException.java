package com.zimmerh0.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class DoctorAlreadyExistsException extends MedicalClinicException {
    public DoctorAlreadyExistsException(Long userId) {
        super("Doctor already exists: "+userId, HttpStatus.CONFLICT);
    }
}

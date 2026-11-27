package com.zimmerh0.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class FacilityAlreadyExistsException extends MedicalClinicException {
    public FacilityAlreadyExistsException(String name) {
        super("Facility already exists"+name, HttpStatus.CONFLICT);
    }
}

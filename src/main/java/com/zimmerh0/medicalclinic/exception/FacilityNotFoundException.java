package com.zimmerh0.medicalclinic.exception;

import org.springframework.http.HttpStatus;

public class FacilityNotFoundException extends MedicalClinicException {
    public FacilityNotFoundException(Long id) {
        super("Facility not found: "+ id, HttpStatus.NOT_FOUND);
    }
}

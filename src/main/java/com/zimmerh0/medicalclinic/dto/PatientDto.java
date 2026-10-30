package com.zimmerh0.medicalclinic.dto;

import com.zimmerh0.medicalclinic.patient.Patient;

import java.time.LocalDate;

public record PatientDto(
        String email,
        String idCardNo,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate birthday
) {
    public static PatientDto from(Patient patient) {
        return new PatientDto(
                patient.getEmail(),
                patient.getIdCardNo(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getPhoneNumber(),
                patient.getBirthday()
        );
    }
}

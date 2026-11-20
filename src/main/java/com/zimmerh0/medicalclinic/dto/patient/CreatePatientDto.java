package com.zimmerh0.medicalclinic.dto.patient;

import java.time.LocalDate;

public record CreatePatientDto(
        Long userId,
        String idCardNo,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate birthday
) {
}
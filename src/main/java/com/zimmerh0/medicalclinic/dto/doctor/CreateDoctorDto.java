package com.zimmerh0.medicalclinic.dto.doctor;

public record CreateDoctorDto(
        Long userId,
        String firstName,
        String lastName,
        String specialization
) {
}
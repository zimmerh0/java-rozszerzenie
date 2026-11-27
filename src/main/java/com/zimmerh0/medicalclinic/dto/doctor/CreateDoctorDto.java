package com.zimmerh0.medicalclinic.dto.doctor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateDoctorDto(
        @NotNull @Positive Long userId,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String specialization
) {
}

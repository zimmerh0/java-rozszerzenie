package com.zimmerh0.medicalclinic.dto.visit;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BookVisitDto(@NotNull @Positive Long patientId) {
}

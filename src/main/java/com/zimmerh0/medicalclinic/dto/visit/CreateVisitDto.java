package com.zimmerh0.medicalclinic.dto.visit;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record CreateVisitDto(
        @NotNull @Positive Long doctorId,
        @NotNull @Positive Long facilityId,
        @NotNull LocalDateTime startDate,
        @NotNull LocalDateTime endDate
) {
}

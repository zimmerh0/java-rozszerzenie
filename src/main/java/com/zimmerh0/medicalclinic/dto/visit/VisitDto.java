package com.zimmerh0.medicalclinic.dto.visit;

import java.time.LocalDateTime;

public record VisitDto(
        Long id,
        Long doctorId,
        Long facilityId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Long patientId
) {
}
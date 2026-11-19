package com.zimmerh0.medicalclinic.dto.doctor;

import com.zimmerh0.medicalclinic.dto.facility.FacilityDto;

import java.util.Set;

public record DoctorDto(
        Long id,
        Long userId,
        String email,
        String firstName,
        String lastName,
        String specialization,
        Set<FacilityDto> facilities
) {
}
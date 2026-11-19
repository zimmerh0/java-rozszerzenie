package com.zimmerh0.medicalclinic.dto.facility;

public record FacilityDto(
        Long id,
        String name,
        String city,
        String postalCode,
        String street,
        String buildingNumber
) {
}
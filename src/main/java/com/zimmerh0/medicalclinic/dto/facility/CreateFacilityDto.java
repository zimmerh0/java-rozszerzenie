package com.zimmerh0.medicalclinic.dto.facility;

public record CreateFacilityDto(
        String name,
        String city,
        String postalCode,
        String street,
        String buildingNumber
) {
}
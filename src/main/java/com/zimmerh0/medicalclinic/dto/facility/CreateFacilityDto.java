package com.zimmerh0.medicalclinic.dto.facility;

import jakarta.validation.constraints.NotBlank;

public record CreateFacilityDto(
        @NotBlank String name,
        @NotBlank String city,
        @NotBlank String postalCode,
        @NotBlank String street,
        @NotBlank String buildingNumber
) {
}

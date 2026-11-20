package com.zimmerh0.medicalclinic.dto.facility;

import com.zimmerh0.medicalclinic.facility.Facility;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FacilityMapper {

    FacilityDto toDto(Facility facility);
}
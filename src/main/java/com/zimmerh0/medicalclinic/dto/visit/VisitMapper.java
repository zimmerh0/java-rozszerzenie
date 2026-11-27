package com.zimmerh0.medicalclinic.dto.visit;

import com.zimmerh0.medicalclinic.visit.Visit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VisitMapper {

    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(target = "facilityId", source = "facility.id")
    @Mapping(target = "patientId", source = "patient.id")
    VisitDto toDto(Visit visit);
}
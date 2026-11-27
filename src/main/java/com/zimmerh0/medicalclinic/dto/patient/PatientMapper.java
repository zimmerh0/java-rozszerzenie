package com.zimmerh0.medicalclinic.dto.patient;

import com.zimmerh0.medicalclinic.patient.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    @Mapping(target = "email", source = "user.email")
    PatientDto toDto(Patient patient);
}
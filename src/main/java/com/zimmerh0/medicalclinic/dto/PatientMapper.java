package com.zimmerh0.medicalclinic.dto;

import com.zimmerh0.medicalclinic.patient.Patient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    PatientDto toDto(Patient patient);

}

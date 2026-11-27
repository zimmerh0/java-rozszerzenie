package com.zimmerh0.medicalclinic.dto.doctor;

import com.zimmerh0.medicalclinic.doctor.Doctor;
import com.zimmerh0.medicalclinic.dto.facility.FacilityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = FacilityMapper.class)
public interface DoctorMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "email", source = "user.email")
    DoctorDto toDto(Doctor doctor);
}
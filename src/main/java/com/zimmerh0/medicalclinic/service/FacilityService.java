package com.zimmerh0.medicalclinic.service;

import com.zimmerh0.medicalclinic.dto.facility.CreateFacilityDto;
import com.zimmerh0.medicalclinic.exception.FacilityAlreadyExistsException;
import com.zimmerh0.medicalclinic.exception.FacilityNotFoundException;
import com.zimmerh0.medicalclinic.facility.Facility;
import com.zimmerh0.medicalclinic.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FacilityService {
    private final FacilityRepository facilityRepository;

    public List<Facility> getAllFacilities(){
        return facilityRepository.findAll();
    }
    public Facility getFacilityById(Long id){
        return facilityRepository.findById(id).orElseThrow(() -> new FacilityNotFoundException(id));
    }
    public Facility addFacility(CreateFacilityDto dto){
        if (facilityRepository.existsByName(dto.name())){
            throw new FacilityAlreadyExistsException(dto.name());
        }
        return facilityRepository.save(Facility.from(dto));
    }
}

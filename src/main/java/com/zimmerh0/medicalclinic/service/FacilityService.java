package com.zimmerh0.medicalclinic.service;

import com.zimmerh0.medicalclinic.dto.facility.CreateFacilityDto;
import com.zimmerh0.medicalclinic.exception.FacilityAlreadyExistsException;
import com.zimmerh0.medicalclinic.exception.FacilityNotFoundException;
import com.zimmerh0.medicalclinic.facility.Facility;
import com.zimmerh0.medicalclinic.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FacilityService {

    private final FacilityRepository facilityRepository;

    public Page<Facility> getAllFacilities(Pageable pageable) {
        return facilityRepository.findAll(pageable);
    }

    public Facility getFacilityById(Long id) {
        return facilityRepository.findById(id)
                .orElseThrow(() -> new FacilityNotFoundException(id));
    }

    @Transactional
    public Facility addFacility(CreateFacilityDto dto) {
        if (facilityRepository.existsByName(dto.name())) {
            throw new FacilityAlreadyExistsException(dto.name());
        }

        return facilityRepository.save(Facility.from(dto));
    }
}

package com.zimmerh0.medicalclinic.controller;

import com.zimmerh0.medicalclinic.dto.facility.CreateFacilityDto;
import com.zimmerh0.medicalclinic.dto.facility.FacilityDto;
import com.zimmerh0.medicalclinic.dto.facility.FacilityMapper;
import com.zimmerh0.medicalclinic.facility.Facility;
import com.zimmerh0.medicalclinic.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facilities")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;
    private final FacilityMapper facilityMapper;

    @GetMapping
    public List<FacilityDto> getFacilities() {
        return facilityService.getAllFacilities().stream()
                .map(facilityMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacilityDto> getFacility(@PathVariable Long id) {
        Facility facility = facilityService.getFacilityById(id);
        return ResponseEntity.ok(facilityMapper.toDto(facility));
    }

    @PostMapping
    public ResponseEntity<FacilityDto> createFacility(@RequestBody CreateFacilityDto dto) {
        if (dto.name() == null || dto.name().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Facility facility = facilityService.addFacility(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(facilityMapper.toDto(facility));
    }
}
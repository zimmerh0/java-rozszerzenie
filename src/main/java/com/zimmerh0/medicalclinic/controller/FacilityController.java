package com.zimmerh0.medicalclinic.controller;

import com.zimmerh0.medicalclinic.dto.PageDto;
import com.zimmerh0.medicalclinic.dto.facility.CreateFacilityDto;
import com.zimmerh0.medicalclinic.dto.facility.FacilityDto;
import com.zimmerh0.medicalclinic.dto.facility.FacilityMapper;
import com.zimmerh0.medicalclinic.facility.Facility;
import com.zimmerh0.medicalclinic.service.FacilityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Placowki", description = "Tworzenie i pobieranie placowek")
@RestController
@RequestMapping("/api/facilities")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;
    private final FacilityMapper facilityMapper;

    @GetMapping
    public PageDto<FacilityDto> getFacilities(
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<FacilityDto> facilities = facilityService.getAllFacilities(pageable)
                .map(facilityMapper::toDto);
        return new PageDto<>(
                facilities.getContent(),
                facilities.getNumber(),
                facilities.getSize(),
                facilities.getTotalElements(),
                facilities.getTotalPages()
        );
    }

    @GetMapping("/{id}")
    public FacilityDto getFacility(@PathVariable Long id) {
        Facility facility = facilityService.getFacilityById(id);
        return facilityMapper.toDto(facility);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FacilityDto createFacility(@Valid @RequestBody CreateFacilityDto dto) {
        Facility facility = facilityService.addFacility(dto);
        return facilityMapper.toDto(facility);
    }
}

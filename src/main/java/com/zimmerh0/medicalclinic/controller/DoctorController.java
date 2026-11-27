package com.zimmerh0.medicalclinic.controller;

import com.zimmerh0.medicalclinic.dto.PageDto;
import com.zimmerh0.medicalclinic.doctor.Doctor;
import com.zimmerh0.medicalclinic.dto.doctor.CreateDoctorDto;
import com.zimmerh0.medicalclinic.dto.doctor.DoctorDto;
import com.zimmerh0.medicalclinic.dto.doctor.DoctorMapper;
import com.zimmerh0.medicalclinic.service.DoctorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Doktorzy", description = "Zarzadzanie doktorami i przypisywanie placowek")
@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;

    @GetMapping
    public PageDto<DoctorDto> getDoctors(
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<DoctorDto> result = doctorService.getAllDoctors(pageable)
                .map(doctorMapper::toDto);
        return new PageDto<>(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @GetMapping("/{id}")
    public DoctorDto getDoctor(@PathVariable Long id) {
        Doctor doctor = doctorService.getDoctorById(id);
        return doctorMapper.toDto(doctor);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorDto createDoctor(
            @Valid @RequestBody CreateDoctorDto dto) {
        Doctor doctor = doctorService.addDoctor(dto);
        return doctorMapper.toDto(doctor);
    }

    @PutMapping("/{doctorId}/facilities/{facilityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assignFacility(
            @PathVariable @Positive Long doctorId,
            @PathVariable @Positive Long facilityId) {
        doctorService.assignFacility(doctorId, facilityId);
    }
}

package com.zimmerh0.medicalclinic.controller;

import com.zimmerh0.medicalclinic.doctor.Doctor;
import com.zimmerh0.medicalclinic.dto.doctor.CreateDoctorDto;
import com.zimmerh0.medicalclinic.dto.doctor.DoctorDto;
import com.zimmerh0.medicalclinic.dto.doctor.DoctorMapper;
import com.zimmerh0.medicalclinic.service.DoctorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;

    @GetMapping
    public List<DoctorDto> getDoctors() {
        return doctorService.getAllDoctors().stream()
                .map(doctorMapper::toDto)
                .toList();
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

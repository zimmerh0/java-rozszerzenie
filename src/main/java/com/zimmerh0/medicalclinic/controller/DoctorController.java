package com.zimmerh0.medicalclinic.controller;

import com.zimmerh0.medicalclinic.doctor.Doctor;
import com.zimmerh0.medicalclinic.dto.doctor.CreateDoctorDto;
import com.zimmerh0.medicalclinic.dto.doctor.DoctorDto;
import com.zimmerh0.medicalclinic.dto.doctor.DoctorMapper;
import com.zimmerh0.medicalclinic.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<DoctorDto> getDoctor(@PathVariable Long id) {
        Doctor doctor = doctorService.getDoctorById(id);
        return ResponseEntity.ok(doctorMapper.toDto(doctor));
    }

    @PostMapping
    public ResponseEntity<DoctorDto> createDoctor(
            @RequestBody CreateDoctorDto dto) {
        if (dto.userId() == null || dto.userId() <= 0) {
            return ResponseEntity.badRequest().build();
        }

        Doctor doctor = doctorService.addDoctor(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorMapper.toDto(doctor));
    }

    @PutMapping("/{doctorId}/facilities/{facilityId}")
    public ResponseEntity<Void> assignFacility(
            @PathVariable Long doctorId,
            @PathVariable Long facilityId) {
        if (doctorId <= 0 || facilityId <= 0) {
            return ResponseEntity.badRequest().build();
        }

        doctorService.assignFacility(doctorId, facilityId);
        return ResponseEntity.noContent().build();
    }
}
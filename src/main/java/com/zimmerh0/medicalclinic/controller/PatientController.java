package com.zimmerh0.medicalclinic.controller;

import com.zimmerh0.medicalclinic.dto.ChangePasswordDto;
import com.zimmerh0.medicalclinic.dto.CreatePatientDto;
import com.zimmerh0.medicalclinic.dto.PatientDto;
import com.zimmerh0.medicalclinic.dto.UpdatePatientDto;
import com.zimmerh0.medicalclinic.patient.Patient;
import com.zimmerh0.medicalclinic.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @GetMapping
    public List<PatientDto> getPatients() {
        return patientService.getAllPatients().stream()
                .map(PatientDto::from)
                .toList();
    }

    @GetMapping("/{email}")
    public ResponseEntity<PatientDto> getPatient(@PathVariable String email) {
        Optional<Patient> patient = patientService.getPatientByEmail(email);
        if (patient.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(PatientDto.from(patient.get()));
    }

    @PostMapping
    public ResponseEntity<PatientDto> createPatient(@RequestBody CreatePatientDto createPatientDto) {
        if (createPatientDto.email() == null || createPatientDto.email().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Patient patient = patientService.addPatient(createPatientDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(PatientDto.from(patient));
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deletePatient(@PathVariable String email) {
        if (!patientService.deletePatientByEmail(email)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{email}")
    public ResponseEntity<PatientDto> updatePatient(
            @PathVariable String email,
            @RequestBody UpdatePatientDto updatePatientDto) {
        Optional<Patient> updatedPatient = patientService.updatePatientByEmail(email, updatePatientDto);
        if (updatedPatient.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(PatientDto.from(updatedPatient.get()));
    }

    @PutMapping("/{email}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable String email,
            @RequestBody ChangePasswordDto dto) {
        if (dto.currentPassword() == null || dto.newPassword()==null || dto.newPassword().isBlank()){
            return ResponseEntity.badRequest().build();
        }


        Optional<Boolean> result = patientService.changePassword(email, dto);
        if (result.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        if (result.get()){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.noContent().build();
    }
}

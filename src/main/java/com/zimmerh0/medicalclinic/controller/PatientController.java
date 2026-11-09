package com.zimmerh0.medicalclinic.controller;

import com.zimmerh0.medicalclinic.dto.CreatePatientDto;
import com.zimmerh0.medicalclinic.dto.PatientDto;
import com.zimmerh0.medicalclinic.dto.PatientMapper;
import com.zimmerh0.medicalclinic.dto.UpdatePatientDto;
import com.zimmerh0.medicalclinic.patient.Patient;
import com.zimmerh0.medicalclinic.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;
    private final PatientMapper patientMapper;

    @GetMapping
    public List<PatientDto> getPatients() {
        return patientService.getAllPatients().stream()
                .map(patientMapper::toDto)
                .toList();
    }

    @GetMapping("/{email}")
    public ResponseEntity<PatientDto> getPatient(@PathVariable String email) {
        Patient patient = patientService.getPatientByEmail(email);
        return ResponseEntity.ok(patientMapper.toDto(patient));
    }

    @PostMapping
    public ResponseEntity<PatientDto> createPatient(@RequestBody CreatePatientDto createPatientDto) {
        if (createPatientDto.email() == null || createPatientDto.email().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Patient patient = patientService.addPatient(createPatientDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(patientMapper.toDto(patient));
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deletePatient(@PathVariable String email) {
        patientService.deletePatientByEmail(email);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{email}")
    public ResponseEntity<PatientDto> updatePatient(@PathVariable String email, @RequestBody UpdatePatientDto updatePatientDto) {
        Patient updatedPatient = patientService.updatePatientByEmail(email, updatePatientDto);
        return ResponseEntity.ok(patientMapper.toDto(updatedPatient));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Void> handlePatientNotFound() {
        return ResponseEntity.notFound().build();
    }
}

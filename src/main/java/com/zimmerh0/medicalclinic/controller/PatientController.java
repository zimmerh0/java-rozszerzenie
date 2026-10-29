package com.zimmerh0.medicalclinic.controller;

import com.zimmerh0.medicalclinic.dto.CreatePatientDto;
import com.zimmerh0.medicalclinic.dto.PatientDto;
import com.zimmerh0.medicalclinic.dto.UpdatePatientDto;
import com.zimmerh0.medicalclinic.patient.Patient;
import com.zimmerh0.medicalclinic.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class PatientController {

    private final PatientService ps;

    public PatientController(PatientService ps) {
        this.ps = ps;
    }

    @GetMapping
    public List<PatientDto> getPatients() {
        return ps.getAllPatients().stream().map(this::toDto).toList();
    }
    @GetMapping("/{email}")
    public ResponseEntity<PatientDto> getPatient(@PathVariable String email) {
        Optional<Patient> patient = ps.getPatientByEmail(email);
        if (patient.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(patient.get()));
    }
    @PostMapping
    public ResponseEntity<PatientDto> createPatient(@RequestBody CreatePatientDto dto) {
        if (dto.email() == null || dto.email().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Patient patient = toPatient(dto);
        if (!ps.addPatient(patient)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(patient));
    }
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deletePatient(@PathVariable String email) {
        if (!ps.deletePatientByEmail(email)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{email}")
    public ResponseEntity<PatientDto> updatePatient(@PathVariable String email, @RequestBody UpdatePatientDto dto) {
        Optional<Patient> updatedPatient = ps.updatePatientByEmail(email, toPatient(email, dto));
        if (updatedPatient.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(updatedPatient.get()));
    }


    private Patient toPatient(CreatePatientDto dto) {
        return new Patient(dto.email(), dto.password(), dto.idCardNo(), dto.firstName(), dto.lastName(), dto.phoneNumber(), dto.birthday());
    }
    private Patient toPatient(String email, UpdatePatientDto dto) {
        return new Patient(email, dto.password(), dto.idCardNo(), dto.firstName(), dto.lastName(), dto.phoneNumber(), dto.birthday());
    }
    private PatientDto toDto(Patient patient) {
        return new PatientDto(patient.getEmail(), patient.getIdCardNo(), patient.getFirstName(), patient.getLastName(), patient.getPhoneNumber(), patient.getBirthday());
    }
}

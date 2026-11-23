package com.zimmerh0.medicalclinic.controller;

import com.zimmerh0.medicalclinic.dto.patient.ChangePasswordCommand;
import com.zimmerh0.medicalclinic.dto.patient.CreatePatientDto;
import com.zimmerh0.medicalclinic.dto.patient.PatientDto;
import com.zimmerh0.medicalclinic.dto.patient.PatientMapper;
import com.zimmerh0.medicalclinic.dto.patient.UpdatePatientDto;
import com.zimmerh0.medicalclinic.patient.Patient;
import com.zimmerh0.medicalclinic.service.ChangePasswordResult;
import com.zimmerh0.medicalclinic.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public PatientDto getPatient(@PathVariable String email) {
        Patient patient = patientService.getPatientByEmail(email);
        return patientMapper.toDto(patient);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDto createPatient(
            @Valid @RequestBody CreatePatientDto createPatientDto) {
        Patient patient = patientService.addPatient(createPatientDto);
        return patientMapper.toDto(patient);
    }

    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePatient(@PathVariable String email) {
        patientService.deletePatientByEmail(email);
    }

    @PutMapping("/{email}")
    public PatientDto updatePatient(
            @PathVariable String email,
            @Valid @RequestBody UpdatePatientDto updatePatientDto) {
        Patient updatedPatient = patientService.updatePatientByEmail(email, updatePatientDto);
        return patientMapper.toDto(updatedPatient);
    }

    @PutMapping("/{email}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable String email,
            @Valid @RequestBody ChangePasswordCommand command) {
        ChangePasswordResult result = patientService.changePassword(email, command);
        return switch (result) {
            case PATIENT_NOT_FOUND -> ResponseEntity.notFound().build();
            case WRONG_PASSWORD -> ResponseEntity.badRequest().build();
            case CHANGED -> ResponseEntity.noContent().build();
        };
    }
}

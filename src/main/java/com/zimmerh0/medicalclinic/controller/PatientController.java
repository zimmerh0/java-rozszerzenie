package com.zimmerh0.medicalclinic.controller;

import com.zimmerh0.medicalclinic.dto.PageDto;
import com.zimmerh0.medicalclinic.dto.patient.ChangePasswordCommand;
import com.zimmerh0.medicalclinic.dto.patient.CreatePatientDto;
import com.zimmerh0.medicalclinic.dto.patient.PatientDto;
import com.zimmerh0.medicalclinic.dto.patient.PatientMapper;
import com.zimmerh0.medicalclinic.dto.patient.UpdatePatientDto;
import com.zimmerh0.medicalclinic.patient.Patient;
import com.zimmerh0.medicalclinic.service.ChangePasswordResult;
import com.zimmerh0.medicalclinic.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Pacjenci", description = "Zarzadzanie pacjentami i zmiana hasla")
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;
    private final PatientMapper patientMapper;

    @Operation(summary = "Pobieranie listy pacjentow z paginacja")
    @GetMapping
    public PageDto<PatientDto> getPatients(
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<PatientDto> result = patientService.getAllPatients(pageable)
                .map(patientMapper::toDto);
        return new PageDto<>(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Operation(summary = "Pobieranie pacjenta po adresie email")
    @GetMapping("/{email}")
    public PatientDto getPatient(@PathVariable String email) {
        Patient patient = patientService.getPatientByEmail(email);
        return patientMapper.toDto(patient);
    }

    @Operation(summary = "Tworzenie pacjenta i przypisanie istniejacego uzytkownika")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDto createPatient(
            @Valid @RequestBody CreatePatientDto createPatientDto) {
        Patient patient = patientService.addPatient(createPatientDto);
        return patientMapper.toDto(patient);
    }

    @Operation(summary = "Usuwanie pacjenta")
    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePatient(@PathVariable String email) {
        patientService.deletePatientByEmail(email);
    }

    @Operation(summary = "Aktualizacja danych pacjenta")
    @PutMapping("/{email}")
    public PatientDto updatePatient(
            @PathVariable String email,
            @Valid @RequestBody UpdatePatientDto updatePatientDto) {
        Patient updatedPatient = patientService.updatePatientByEmail(email, updatePatientDto);
        return patientMapper.toDto(updatedPatient);
    }

    @Operation(summary = "Zmiana hasla pacjenta")
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

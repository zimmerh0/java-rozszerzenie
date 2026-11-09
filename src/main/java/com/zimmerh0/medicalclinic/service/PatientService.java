package com.zimmerh0.medicalclinic.service;

import com.zimmerh0.medicalclinic.dto.CreatePatientDto;
import com.zimmerh0.medicalclinic.dto.UpdatePatientDto;
import com.zimmerh0.medicalclinic.patient.Patient;
import com.zimmerh0.medicalclinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient getPatientByEmail(String email) {
        Patient patient = patientRepository.findByEmail(email);
        if (patient == null) {
            throw new NoSuchElementException("Patient not found: " + email);
        }
        return patient;
    }

    public Patient addPatient(CreatePatientDto createPatientDto) {
        Patient patient = Patient.from(createPatientDto);
        return patientRepository.save(patient);
    }

    public boolean deletePatientByEmail(String email) {
        getPatientByEmail(email);
        patientRepository.deleteByEmail(email);
        return true;
    }

    public Patient updatePatientByEmail(String email, UpdatePatientDto updatePatientDto) {
        Patient patient = getPatientByEmail(email);
        patient.update(updatePatientDto);
        return patientRepository.save(patient);
    }
}

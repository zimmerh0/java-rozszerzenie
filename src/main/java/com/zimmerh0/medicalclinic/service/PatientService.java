package com.zimmerh0.medicalclinic.service;

import com.zimmerh0.medicalclinic.dto.ChangePasswordCommand;
import com.zimmerh0.medicalclinic.dto.CreatePatientDto;
import com.zimmerh0.medicalclinic.dto.UpdatePatientDto;
import com.zimmerh0.medicalclinic.exception.PatientAlreadyExistsException;
import com.zimmerh0.medicalclinic.patient.Patient;
import com.zimmerh0.medicalclinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Optional<Patient> getPatientByEmail(String email) {
        return patientRepository.findByEmail(email);
    }

    public Patient addPatient(CreatePatientDto createPatientDto) {
        Patient patient = Patient.from(createPatientDto);
        if (!patientRepository.saveIfAbsent(patient)) {
            throw new PatientAlreadyExistsException(patient.getEmail());
        }

        return patient;
    }

    public boolean deletePatientByEmail(String email) {
        if (patientRepository.findByEmail(email).isEmpty()) {
            return false;
        }

        patientRepository.deleteByEmail(email);
        return true;
    }

    public Optional<Patient> updatePatientByEmail(String email, UpdatePatientDto updatePatientDto) {
        Optional<Patient> foundPatient = patientRepository.findByEmail(email);
        if (foundPatient.isEmpty()) {
            return Optional.empty();
        }

        Patient patient = foundPatient.get();
        patient.update(updatePatientDto);
        return Optional.of(patientRepository.save(patient));
    }

    public ChangePasswordResult changePassword(String email, ChangePasswordCommand command) {
        Optional<Patient> found = patientRepository.findByEmail(email);

        if (found.isEmpty()) {
            return ChangePasswordResult.PATIENT_NOT_FOUND;
        }

        Patient patient = found.get();
        if (!patient.getPassword().equals(command.currentPassword())) {
            return ChangePasswordResult.WRONG_PASSWORD;
        }

        patient.setPassword(command.newPassword());
        patientRepository.save(patient);
        return ChangePasswordResult.CHANGED;
    }
}

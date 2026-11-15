package com.zimmerh0.medicalclinic.service;

import com.zimmerh0.medicalclinic.dto.ChangePasswordCommand;
import com.zimmerh0.medicalclinic.dto.CreatePatientDto;
import com.zimmerh0.medicalclinic.dto.UpdatePatientDto;
import com.zimmerh0.medicalclinic.exception.PatientAlreadyExistsException;
import com.zimmerh0.medicalclinic.exception.PatientNotFoundException;
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

    public Patient getPatientByEmail(String email) {
        return patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
    }

    public Patient addPatient(CreatePatientDto createPatientDto) {
        String email = createPatientDto.email();
        if (patientRepository.existsByEmail(email)) {
            throw new PatientAlreadyExistsException(email);
        }
        return patientRepository.save(Patient.from(createPatientDto));
    }

    public boolean deletePatientByEmail(String email) {
        Patient patient = getPatientByEmail(email);
        patientRepository.delete(patient);
        return true;
    }

    public Patient updatePatientByEmail(String email, UpdatePatientDto updatePatientDto) {
        Patient patient = getPatientByEmail(email);
        patient.update(updatePatientDto);
        return patientRepository.save(patient);
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

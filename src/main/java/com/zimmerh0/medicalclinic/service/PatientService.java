package com.zimmerh0.medicalclinic.service;

import com.zimmerh0.medicalclinic.patient.Patient;
import com.zimmerh0.medicalclinic.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {
    
    private final PatientRepository pr;

    public PatientService(PatientRepository pr) {
        this.pr = pr;
    }
    public List<Patient> getAllPatients() {
        return pr.findAll();
    }
    public Optional<Patient> getPatientByEmail(String email) {
        return pr.findByEmail(email);
    }
    public boolean addPatient(Patient patient) {
        if (pr.findByEmail(patient.getEmail()).isPresent()) {
            return false;
        }
        pr.save(patient);
        return true;
    }

    public boolean deletePatientByEmail(String email) {
        if (pr.findByEmail(email).isEmpty()) {
            return false;
        }
        pr.deleteByEmail(email);
        return true;
    }

    public Optional<Patient> updatePatientByEmail(String email, Patient updatedPatient) {
        Optional<Patient> foundPatient = pr.findByEmail(email);
        if (foundPatient.isEmpty()) {
            return Optional.empty();
        }
        Patient patient = foundPatient.get();
        patient.setPassword(updatedPatient.getPassword());
        patient.setIdCardNo(updatedPatient.getIdCardNo());
        patient.setFirstName(updatedPatient.getFirstName());
        patient.setLastName(updatedPatient.getLastName());
        patient.setPhoneNumber(updatedPatient.getPhoneNumber());
        patient.setBirthday(updatedPatient.getBirthday());
        pr.save(patient);
        return Optional.of(patient);
    }
}

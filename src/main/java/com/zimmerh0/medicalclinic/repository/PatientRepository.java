package com.zimmerh0.medicalclinic.repository;

import com.zimmerh0.medicalclinic.patient.Patient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PatientRepository {
    private final Map<String, Patient> patients = new ConcurrentHashMap<>();

    public void save(Patient p){
        patients.put(p.getEmail(), p);
    }
    public Optional<Patient> findByEmail(String email){
        return Optional.ofNullable(patients.get(email));
    }
    public List<Patient> findAll() {
        return List.copyOf(patients.values());
    }
    public void deleteByEmail(String email){
        patients.remove(email);
    }
}

package com.zimmerh0.medicalclinic.repository;

import com.zimmerh0.medicalclinic.patient.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByUser_Email(String email);

    boolean existsByUser_Id(Long userId);
}
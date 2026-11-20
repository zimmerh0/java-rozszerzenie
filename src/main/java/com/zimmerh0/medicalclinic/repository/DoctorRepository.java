package com.zimmerh0.medicalclinic.repository;

import com.zimmerh0.medicalclinic.doctor.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    boolean existsByUserId(Long userId);
}

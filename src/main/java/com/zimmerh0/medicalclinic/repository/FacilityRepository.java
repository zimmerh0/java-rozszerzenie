package com.zimmerh0.medicalclinic.repository;

import com.zimmerh0.medicalclinic.facility.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityRepository extends JpaRepository<Facility, Long> {
    boolean existsByName(String name);
}

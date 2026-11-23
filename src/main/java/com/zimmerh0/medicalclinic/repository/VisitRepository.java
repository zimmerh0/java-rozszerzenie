package com.zimmerh0.medicalclinic.repository;

import com.zimmerh0.medicalclinic.visit.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    List<Visit> findByPatient_IdOrderByStartDateAsc(Long patientId);

    boolean existsByDoctor_IdAndStartDateLessThanAndEndDateGreaterThan(
            Long doctorId,
            LocalDateTime endDate,
            LocalDateTime startDate);
}
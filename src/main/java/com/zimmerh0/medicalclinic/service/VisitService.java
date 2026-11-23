package com.zimmerh0.medicalclinic.service;

import com.zimmerh0.medicalclinic.doctor.Doctor;
import com.zimmerh0.medicalclinic.dto.visit.BookVisitDto;
import com.zimmerh0.medicalclinic.dto.visit.CreateVisitDto;
import com.zimmerh0.medicalclinic.exception.DoctorNotFoundException;
import com.zimmerh0.medicalclinic.facility.Facility;
import com.zimmerh0.medicalclinic.patient.Patient;
import com.zimmerh0.medicalclinic.repository.DoctorRepository;
import com.zimmerh0.medicalclinic.repository.PatientRepository;
import com.zimmerh0.medicalclinic.repository.VisitRepository;
import com.zimmerh0.medicalclinic.visit.Visit;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VisitService {

    private final VisitRepository visitRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final FacilityService facilityService;

    @Transactional
    public Visit addVisit(CreateVisitDto dto) {
        Doctor doctor = doctorRepository.findById(dto.doctorId())
                .orElseThrow(() -> new DoctorNotFoundException(dto.doctorId()));

        Facility facility = facilityService.getFacilityById(dto.facilityId());

        validateVisitDates(dto.startDate(), dto.endDate());

        boolean overlaps = visitRepository
                .existsByDoctor_IdAndStartDateLessThanAndEndDateGreaterThan(
                        doctor.getId(),
                        dto.endDate(),
                        dto.startDate()
                );

        if (overlaps) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Doctor already has a visit at this time");
        }

        Visit visit = new Visit(
                doctor,
                facility,
                dto.startDate(),
                dto.endDate()
        );

        return visitRepository.save(visit);
    }

    @Transactional
    public void bookVisit(Long visitId, BookVisitDto dto) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Visit not found"));

        if (!visit.getStartDate().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Visit is in the past");
        }

        if (visit.getPatient() != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Visit is already booked");
        }

        Patient patient = patientRepository.findById(dto.patientId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Patient not found"));

        visit.setPatient(patient);
        visitRepository.save(visit);
    }

    private void validateVisitDates(LocalDateTime startDate, LocalDateTime endDate) {
        if (!startDate.isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Visit must be in the future");
        }

        if (!endDate.isAfter(startDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "End must be after start");
        }

        if (!isQuarterHour(startDate) || !isQuarterHour(endDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Time must be a full quarter hour");
        }
    }

    private boolean isQuarterHour(LocalDateTime dateTime) {
        return dateTime.getMinute() % 15 == 0
                && dateTime.getSecond() == 0
                && dateTime.getNano() == 0;
    }
}

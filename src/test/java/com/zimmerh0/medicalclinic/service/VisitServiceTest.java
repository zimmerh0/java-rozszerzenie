package com.zimmerh0.medicalclinic.service;

import com.zimmerh0.medicalclinic.doctor.Doctor;
import com.zimmerh0.medicalclinic.dto.doctor.CreateDoctorDto;
import com.zimmerh0.medicalclinic.dto.facility.CreateFacilityDto;
import com.zimmerh0.medicalclinic.dto.visit.BookVisitDto;
import com.zimmerh0.medicalclinic.dto.visit.CreateVisitDto;
import com.zimmerh0.medicalclinic.exception.DoctorNotFoundException;
import com.zimmerh0.medicalclinic.exception.FacilityNotFoundException;
import com.zimmerh0.medicalclinic.facility.Facility;
import com.zimmerh0.medicalclinic.repository.DoctorRepository;
import com.zimmerh0.medicalclinic.repository.PatientRepository;
import com.zimmerh0.medicalclinic.repository.VisitRepository;
import com.zimmerh0.medicalclinic.user.User;
import com.zimmerh0.medicalclinic.visit.Visit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class VisitServiceTest {

    private VisitService visitService;
    private VisitRepository visitRepository;
    private DoctorRepository doctorRepository;
    private PatientRepository patientRepository;
    private FacilityService facilityService;

    @BeforeEach
    void setUp() {
        visitRepository = Mockito.mock(VisitRepository.class);
        doctorRepository = Mockito.mock(DoctorRepository.class);
        patientRepository = Mockito.mock(PatientRepository.class);
        facilityService = Mockito.mock(FacilityService.class);
        visitService = new VisitService(visitRepository, doctorRepository, patientRepository, facilityService);
    }

    @Test
    void addVisit_DoctorMissing_ThrowsException() {
        //given
        LocalDateTime startDate = LocalDate.now().plusDays(1).atTime(10, 0);
        CreateVisitDto dto = new CreateVisitDto(1L, 2L, startDate, startDate.plusMinutes(45));
        when(doctorRepository.findById(dto.doctorId())).thenReturn(Optional.empty());

        //when
        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
                () -> visitService.addVisit(dto));

        //then
        assertEquals("Doctor not found: " + dto.doctorId(), exception.getMessage());
        verifyNoInteractions(visitRepository, facilityService);
    }

    @Test
    void addVisit_FacilityMissing_ThrowsException() {
        //given
        LocalDateTime startDate = LocalDate.now().plusDays(1).atTime(10, 0);
        CreateVisitDto dto = new CreateVisitDto(1L, 2L, startDate, startDate.plusMinutes(45));
        Doctor doctor = Doctor.from(new CreateDoctorDto(1L, "Jan", "Nowak", "Kardiolog"),
                new User("jan@mail.com", "123"));
        when(doctorRepository.findById(dto.doctorId())).thenReturn(Optional.of(doctor));
        when(facilityService.getFacilityById(dto.facilityId()))
                .thenThrow(new FacilityNotFoundException(dto.facilityId()));

        //when
        FacilityNotFoundException exception = assertThrows(FacilityNotFoundException.class,
                () -> visitService.addVisit(dto));

        //then
        assertEquals("Facility not found: " + dto.facilityId(), exception.getMessage());
        verifyNoInteractions(visitRepository);
    }

    @Test
    void bookVisit_VisitMissing_ThrowsException() {
        //given
        Long visitId = 1L;
        BookVisitDto dto = new BookVisitDto(2L);
        when(visitRepository.findById(visitId)).thenReturn(Optional.empty());

        //when
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> visitService.bookVisit(visitId, dto));

        //then
        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode()),
                () -> assertEquals("Visit not found", exception.getReason())
        );
        verify(visitRepository, never()).save(any(Visit.class));
        verifyNoInteractions(patientRepository);
    }

    @Test
    void bookVisit_PatientMissing_VisitRemainsAvailable() {
        //given
        Long visitId = 1L;
        BookVisitDto dto = new BookVisitDto(2L);
        Visit visit = visit(LocalDate.now().plusDays(1).atTime(10, 0));
        when(visitRepository.findById(visitId)).thenReturn(Optional.of(visit));
        when(patientRepository.findById(dto.patientId())).thenReturn(Optional.empty());

        //when
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> visitService.bookVisit(visitId, dto));

        //then
        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode()),
                () -> assertEquals("Patient not found", exception.getReason()),
                () -> assertNull(visit.getPatient())
        );
        verify(visitRepository, never()).save(any(Visit.class));
    }

    @Test
    void bookVisit_VisitInPast_ThrowsExceptionAndPatientNotAssigned() {
        //given
        Long visitId = 1L;
        BookVisitDto dto = new BookVisitDto(2L);
        Visit visit = visit(LocalDate.now().minusDays(1).atTime(10, 0));
        when(visitRepository.findById(visitId)).thenReturn(Optional.of(visit));

        //when
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> visitService.bookVisit(visitId, dto));

        //then
        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode()),
                () -> assertEquals("Visit is in the past", exception.getReason()),
                () -> assertNull(visit.getPatient())
        );
        verify(visitRepository, never()).save(any(Visit.class));
        verifyNoInteractions(patientRepository);
    }

    private Visit visit(LocalDateTime startDate) {
        Doctor doctor = Doctor.from(new CreateDoctorDto(1L, "Jan", "Nowak", "Kardiolog"),
                new User("jan@mail.com", "123"));
        Facility facility = Facility.from(new CreateFacilityDto(
                "Przychodnia Centrum", "Warszawa", "00-001", "Polna", "10"));
        return new Visit(doctor, facility, startDate, startDate.plusMinutes(45));
    }
}

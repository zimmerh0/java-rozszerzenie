package com.zimmerh0.medicalclinic.service;

import com.zimmerh0.medicalclinic.doctor.Doctor;
import com.zimmerh0.medicalclinic.dto.doctor.CreateDoctorDto;
import com.zimmerh0.medicalclinic.dto.facility.CreateFacilityDto;
import com.zimmerh0.medicalclinic.exception.DoctorAlreadyExistsException;
import com.zimmerh0.medicalclinic.exception.DoctorNotFoundException;
import com.zimmerh0.medicalclinic.exception.FacilityNotFoundException;
import com.zimmerh0.medicalclinic.exception.UserNotFoundException;
import com.zimmerh0.medicalclinic.facility.Facility;
import com.zimmerh0.medicalclinic.repository.DoctorRepository;
import com.zimmerh0.medicalclinic.repository.UserRepository;
import com.zimmerh0.medicalclinic.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class DoctorServiceTest {

    private DoctorService doctorService;
    private DoctorRepository doctorRepository;
    private UserRepository userRepository;
    private FacilityService facilityService;

    @BeforeEach
    void setUp() {
        doctorRepository = Mockito.mock(DoctorRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        facilityService = Mockito.mock(FacilityService.class);
        doctorService = new DoctorService(doctorRepository, userRepository, facilityService);
    }

    @Test
    void getAllDoctors_DoctorsExist_DoctorsReturned() {
        //given
        Doctor doctor1 = Doctor.from(new CreateDoctorDto(1L, "Jan", "Nowak", "Kardiolog"),
                new User("jan@mail.com", "123"));
        Doctor doctor2 = Doctor.from(new CreateDoctorDto(2L, "Anna", "Kowalska", "Dentysta"),
                new User("anna@mail.com", "456"));
        Pageable pageable = PageRequest.of(0, 20);
        when(doctorRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(doctor1, doctor2), pageable, 2));

        //when
        Page<Doctor> result = doctorService.getAllDoctors(pageable);

        //then
        assertAll(
                () -> assertEquals(2, result.getContent().size()),
                () -> assertSame(doctor1, result.getContent().get(0)),
                () -> assertSame(doctor2, result.getContent().get(1))
        );
        verify(doctorRepository).findAll(pageable);
    }

    @Test
    void getDoctorById_DoctorMissing_ThrowsException() {
        //given
        Long id = 1L;
        when(doctorRepository.findById(id)).thenReturn(Optional.empty());

        //when
        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
                () -> doctorService.getDoctorById(id));

        //then
        assertEquals("Doctor not found: " + id, exception.getMessage());
    }

    @Test
    void addDoctor_UserMissing_ThrowsException() {
        //given
        CreateDoctorDto dto = new CreateDoctorDto(1L, "Jan", "Nowak", "Kardiolog");
        when(userRepository.findById(dto.userId())).thenReturn(Optional.empty());

        //when
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> doctorService.addDoctor(dto));

        //then
        assertEquals("User not found: " + dto.userId(), exception.getMessage());
        verifyNoInteractions(doctorRepository);
    }

    @Test
    void addDoctor_UserAlreadyHasDoctor_ThrowsException() {
        //given
        CreateDoctorDto dto = new CreateDoctorDto(1L, "Jan", "Nowak", "Kardiolog");
        User user = new User("jan@mail.com", "123");
        ReflectionTestUtils.setField(user, "id", dto.userId());
        when(userRepository.findById(dto.userId())).thenReturn(Optional.of(user));
        when(doctorRepository.existsByUserId(dto.userId())).thenReturn(true);

        //when
        DoctorAlreadyExistsException exception = assertThrows(DoctorAlreadyExistsException.class,
                () -> doctorService.addDoctor(dto));

        //then
        assertEquals("Doctor already exists: " + dto.userId(), exception.getMessage());
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void assignFacility_DoctorMissing_ThrowsException() {
        //given
        Long doctorId = 1L;
        Long facilityId = 2L;
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        //when
        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
                () -> doctorService.assignFacility(doctorId, facilityId));

        //then
        assertEquals("Doctor not found: " + doctorId, exception.getMessage());
        verify(doctorRepository, never()).save(any(Doctor.class));
        verifyNoInteractions(facilityService);
    }

    @Test
    void assignFacility_FacilityMissing_ThrowsException() {
        //given
        Long doctorId = 1L;
        Long facilityId = 2L;
        Doctor doctor = Doctor.from(new CreateDoctorDto(1L, "Jan", "Nowak", "Kardiolog"),
                new User("jan@mail.com", "123"));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(facilityService.getFacilityById(facilityId)).thenThrow(new FacilityNotFoundException(facilityId));

        //when
        FacilityNotFoundException exception = assertThrows(FacilityNotFoundException.class,
                () -> doctorService.assignFacility(doctorId, facilityId));

        //then
        assertEquals("Facility not found: " + facilityId, exception.getMessage());
        assertTrue(doctor.getFacilities().isEmpty());
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void assignFacility_FacilityAssignedTwice_NoDuplicateRelation() {
        //given
        Long doctorId = 1L;
        Long facilityId = 2L;
        Doctor doctor = Doctor.from(new CreateDoctorDto(1L, "Jan", "Nowak", "Kardiolog"),
                new User("jan@mail.com", "123"));
        Facility facility = Facility.from(new CreateFacilityDto(
                "Przychodnia Centrum", "Warszawa", "00-001", "Polna", "10"));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(facilityService.getFacilityById(facilityId)).thenReturn(facility);

        //when
        doctorService.assignFacility(doctorId, facilityId);
        doctorService.assignFacility(doctorId, facilityId);

        //then
        assertAll(
                () -> assertEquals(Set.of(facility), doctor.getFacilities()),
                () -> assertEquals(Set.of(doctor), facility.getDoctors())
        );
    }
}

package com.zimmerh0.medicalclinic.service;

import com.zimmerh0.medicalclinic.dto.CreatePatientDto;
import com.zimmerh0.medicalclinic.patient.Patient;
import com.zimmerh0.medicalclinic.repository.PatientRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PatientServiceTest {

    private PatientService patientService;
    private PatientRepository patientRepository;

    @BeforeEach
    void setUp(){
        patientRepository = Mockito.mock(PatientRepository.class);
        patientService = new PatientService(patientRepository);
    }

    @Test
    void getAllPatients_PatientExists_PatientsReturned(){
        //given
        Patient patient1 = new Patient("mail1@mail.com", "123", "abc123", "Ania", "Kowalska", "123456789", LocalDate.of(2000, 12, 1));
        Patient patient2 = new Patient("mail2@mail.com", "543", "hnj534", "Jan", "Nowak", "987654321", LocalDate.of(2005, 10, 12));
        when(patientRepository.findAll()).thenReturn(List.of(patient1, patient2));

        //when
        List<Patient> result = patientService.getAllPatients();

        //then
        Assertions.assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals("mail1@mail.com", result.get(0).getEmail()),
                () -> assertEquals("mail2@mail.com", result.get(1).getEmail())
        );
    }

    @Test
    void getPatientByEmail_PatientExist(){
        //given
        String email = "mail1@mail.com";
        Patient patient = new Patient(email, "123", "abc123", "Ania", "Kowalska", "123456789", LocalDate.of(2000, 12, 1));
        when(patientRepository.findByEmail(email)).thenReturn(patient);
        //when
        Patient result = patientService.getPatientByEmail(email);
        //then
        assertEquals(email, result.getEmail());
    }

    @Test
    void addPatient_PatientCreated_PatientSavedAndReturned(){
        //given
        CreatePatientDto dto = new CreatePatientDto("email@mail.com", "123", "abc123", "Ania", "Kowalska", "123456789", LocalDate.of(2000, 12, 1));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));
        //when
        Patient result = patientService.addPatient(dto);
        //then
        assertAll(
                () -> assertEquals("email@mail.com", result.getEmail()),
                () -> assertEquals("123", result.getPassword()),
                () -> assertEquals("abc123", result.getIdCardNo()),
                () -> assertEquals("Ania", result.getFirstName()),
                () -> assertEquals("Kowalska", result.getLastName()),
                () -> assertEquals("123456789", result.getPhoneNumber()),
                () -> assertEquals(LocalDate.of(2000, 12, 1), result.getBirthday())
        );
    }

    @Test
    void deletePatientByEmail_PatientDeleted_Verified(){
        //given
        String email = "mail@mail.com";
        Patient patient = new Patient(email, "123", "abc123", "Ania", "Kowalska", "123456789", LocalDate.of(2000, 12, 1));
        when(patientRepository.findByEmail(email)).thenReturn(patient);
        //when
        boolean result = patientService.deletePatientByEmail(email);
        //then
        assertTrue(result);
        verify(patientRepository).deleteByEmail(email);
    }

    @Test
    void getPatientByEmail_PatientMissing_ThrowsException(){
        //given
        String email = "uhegfu@mail.com";
        when(patientRepository.findByEmail(email)).thenReturn(null);
        //when
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> patientService.getPatientByEmail(email));
        //then
        assertEquals("Patient not found: " + email, exception.getMessage());
    }

}

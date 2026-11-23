package com.zimmerh0.medicalclinic.service;

import com.zimmerh0.medicalclinic.dto.patient.CreatePatientDto;
import com.zimmerh0.medicalclinic.exception.PatientAlreadyExistsException;
import com.zimmerh0.medicalclinic.exception.PatientNotFoundException;
import com.zimmerh0.medicalclinic.patient.Patient;
import com.zimmerh0.medicalclinic.repository.PatientRepository;
import com.zimmerh0.medicalclinic.repository.UserRepository;
import com.zimmerh0.medicalclinic.user.User;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PatientServiceTest {

    private PatientService patientService;
    private PatientRepository patientRepository;
    private UserRepository userRepository;

    @BeforeEach
    void setUp(){
        patientRepository = Mockito.mock(PatientRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        patientService = new PatientService(patientRepository, userRepository);
    }

    @Test
    void getAllPatients_PatientExists_PatientsReturned(){
        //given
        Patient patient1 = new Patient(new User("mail1@mail.com", "123"), "abc123", "Ania", "Kowalska", "123456789", LocalDate.of(2000, 12, 1));
        Patient patient2 = new Patient(new User("mail2@mail.com", "543"), "hnj534", "Jan", "Nowak", "987654321", LocalDate.of(2005, 10, 12));
        when(patientRepository.findAll()).thenReturn(List.of(patient1, patient2));

        //when
        List<Patient> result = patientService.getAllPatients();

        //then
        Assertions.assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals("mail1@mail.com", result.get(0).getUser().getEmail()),
                () -> assertEquals("mail2@mail.com", result.get(1).getUser().getEmail())
        );
    }

    @Test
    void getPatientByEmail_PatientExist(){
        //given
        String email = "mail1@mail.com";
        Patient patient = new Patient(new User(email, "123"), "abc123", "Ania", "Kowalska", "123456789", LocalDate.of(2000, 12, 1));
        when(patientRepository.findByUser_Email(email)).thenReturn(Optional.of(patient));
        //when
        Patient result = patientService.getPatientByEmail(email);
        //then
        assertEquals(email, result.getUser().getEmail());
    }

    @Test
    void addPatient_PatientCreated_PatientSavedAndReturned(){
        //given
        CreatePatientDto dto = new CreatePatientDto(1L, "abc123", "Ania", "Kowalska", "123456789", LocalDate.of(2000, 12, 1));
        User user = user();
        when(userRepository.findById(dto.userId())).thenReturn(Optional.of(user));
        when(patientRepository.existsByUser_Id(dto.userId())).thenReturn(false);
        when(patientRepository.save(any(Patient.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        //when
        Patient result = patientService.addPatient(dto);
        //then
        assertAll(
                () -> assertEquals("email@mail.com", result.getUser().getEmail()),
                () -> assertEquals("123", result.getUser().getPassword()),
                () -> assertEquals("abc123", result.getIdCardNo()),
                () -> assertEquals("Ania", result.getFirstName()),
                () -> assertEquals("Kowalska", result.getLastName()),
                () -> assertEquals("123456789", result.getPhoneNumber()),
                () -> assertEquals(LocalDate.of(2000, 12, 1), result.getBirthday())
        );
        verify(patientRepository).save(result);
    }

    @Test
    void addPatient_EmailExists_ThrowsException() {
        CreatePatientDto dto = new CreatePatientDto(1L, "abc123", "Ania", "Kowalska", "123456789", LocalDate.of(2000, 12, 1));
        User user = user();
        when(userRepository.findById(dto.userId())).thenReturn(Optional.of(user));
        when(patientRepository.existsByUser_Id(dto.userId())).thenReturn(true);

        PatientAlreadyExistsException exception = assertThrows(PatientAlreadyExistsException.class,
                () -> patientService.addPatient(dto));

        assertEquals("Patient already exists: " + user.getEmail(), exception.getMessage());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void deletePatientByEmail_PatientDeleted_Verified(){
        //given
        String email = "mail@mail.com";
        Patient patient = new Patient(new User(email, "123"), "abc123", "Ania", "Kowalska", "123456789", LocalDate.of(2000, 12, 1));
        when(patientRepository.findByUser_Email(email)).thenReturn(Optional.of(patient));
        //when
        patientService.deletePatientByEmail(email);
        //then
        verify(patientRepository).delete(patient);
    }

    @Test
    void getPatientByEmail_PatientMissing_ThrowsException(){
        //given
        String email = "uhegfu@mail.com";
        when(patientRepository.findByUser_Email(email)).thenReturn(Optional.empty());
        //when
        PatientNotFoundException exception = assertThrows(PatientNotFoundException.class, () -> patientService.getPatientByEmail(email));
        //then
        assertEquals("Patient not found: " + email, exception.getMessage());
    }

    private User user() {
        User user = new User("email@mail.com", "123");
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }
}

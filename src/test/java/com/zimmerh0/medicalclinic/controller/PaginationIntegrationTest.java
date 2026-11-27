package com.zimmerh0.medicalclinic.controller;

import com.zimmerh0.medicalclinic.doctor.Doctor;
import com.zimmerh0.medicalclinic.dto.doctor.CreateDoctorDto;
import com.zimmerh0.medicalclinic.dto.patient.CreatePatientDto;
import com.zimmerh0.medicalclinic.patient.Patient;
import com.zimmerh0.medicalclinic.repository.DoctorRepository;
import com.zimmerh0.medicalclinic.repository.PatientRepository;
import com.zimmerh0.medicalclinic.repository.UserRepository;
import com.zimmerh0.medicalclinic.user.User;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PaginationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private DoctorRepository doctorRepository;

    @ParameterizedTest
    @ValueSource(strings = {"/api/users", "/api/patients", "/api/doctors"})
    void getList_NoParameters_DefaultPageReturned(String endpoint) throws Exception {
        //given
        createRecords(endpoint, 21);

        //when + then
        mockMvc.perform(get(endpoint))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(20))
                .andExpect(jsonPath("$.content[0].email").value("user1@mail.com"))
                .andExpect(jsonPath("$.content[0].password").doesNotExist())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(21))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/users", "/api/patients", "/api/doctors"})
    void getList_SecondPageRequested_CorrectRecordsReturned(String endpoint) throws Exception {
        //given
        createRecords(endpoint, 3);

        //when + then
        mockMvc.perform(get(endpoint).param("page", "1").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].email").value("user3@mail.com"))
                .andExpect(jsonPath("$.content[0].password").doesNotExist())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/users", "/api/patients", "/api/doctors"})
    void getList_RecordsMissing_EmptyPageReturned(String endpoint) throws Exception {
        //when + then
        mockMvc.perform(get(endpoint))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/users", "/api/patients", "/api/doctors"})
    void getList_PageBeyondLast_EmptyContentAndCorrectTotalsReturned(String endpoint) throws Exception {
        //given
        createRecords(endpoint, 3);

        //when + then
        mockMvc.perform(get(endpoint).param("page", "5").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.page").value(5))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/users", "/api/patients", "/api/doctors"})
    void getList_NegativePage_ReturnsBadRequest(String endpoint) throws Exception {
        mockMvc.perform(get(endpoint).param("page", "-1"))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/users", "/api/patients", "/api/doctors"})
    void getList_ZeroSize_ReturnsBadRequest(String endpoint) throws Exception {
        mockMvc.perform(get(endpoint).param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/users", "/api/patients", "/api/doctors"})
    void getList_SizeAboveLimit_ReturnsBadRequest(String endpoint) throws Exception {
        mockMvc.perform(get(endpoint).param("size", "101"))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/users", "/api/patients", "/api/doctors"})
    void getList_MaximumSize_Accepted(String endpoint) throws Exception {
        //given
        createRecords(endpoint, 3);

        //when + then
        mockMvc.perform(get(endpoint).param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(100))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    private void createRecords(String endpoint, int count) {
        for (int i = 1; i <= count; i++) {
            User user = userRepository.save(new User("user" + i + "@mail.com", "123"));
            if (endpoint.equals("/api/patients")) {
                patientRepository.save(Patient.from(new CreatePatientDto(
                        user.getId(), "ABC" + i, "Anna", "Nowak", "123456789", LocalDate.of(2000, 1, 2)), user));
            } else if (endpoint.equals("/api/doctors")) {
                doctorRepository.save(Doctor.from(new CreateDoctorDto(
                        user.getId(), "Jan", "Nowak", "Kardiolog"), user));
            }
        }
    }
}

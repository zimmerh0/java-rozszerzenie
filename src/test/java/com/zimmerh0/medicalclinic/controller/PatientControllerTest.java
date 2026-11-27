package com.zimmerh0.medicalclinic.controller;

import com.zimmerh0.medicalclinic.dto.patient.CreatePatientDto;
import com.zimmerh0.medicalclinic.dto.patient.UpdatePatientDto;
import com.zimmerh0.medicalclinic.exception.PatientAlreadyExistsException;
import com.zimmerh0.medicalclinic.exception.PatientNotFoundException;
import com.zimmerh0.medicalclinic.patient.Patient;
import com.zimmerh0.medicalclinic.user.User;
import com.zimmerh0.medicalclinic.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PatientControllerTest {

    @MockitoBean
    PatientService service;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void getAllPatients_PatientsExists_ReturnsPatients() throws Exception{
        //given
        Patient patient = new Patient(new User("mail@mail.com", "123"), "abc123", "Ania", "Kowalska", "123456789", LocalDate.of(2000, 12, 1));
        Pageable pageable = PageRequest.of(0, 20, Sort.by("id").ascending());
        when(service.getAllPatients(pageable)).thenReturn(new PageImpl<>(List.of(patient), pageable, 1));

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].email").value("mail@mail.com"))
                .andExpect(jsonPath("$.content[0].password").doesNotExist())
                .andExpect(jsonPath("$.content[0].idCardNo").value("abc123"))
                .andExpect(jsonPath("$.content[0].firstName").value("Ania"))
                .andExpect(jsonPath("$.content[0].lastName").value("Kowalska"))
                .andExpect(jsonPath("$.content[0].phoneNumber").value("123456789"))
                .andExpect(jsonPath("$.content[0].birthday").value("2000-12-01"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void getPatient_PatientExists_ReturnsPatient() throws Exception {
        String email = "ania@mail.com";
        Patient patient = new Patient(new User(email, "123"), "abc123", "Ania", "Kowalska", "123456789", LocalDate.of(2000, 12, 1));
        when(service.getPatientByEmail(email)).thenReturn(patient);

        mockMvc.perform(get("/api/patients/{email}", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.firstName").value("Ania"))
                .andExpect(jsonPath("$.birthday").value("2000-12-01"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void getPatient_PatientMissing_ReturnsNotFound() throws Exception {
        String email = "missing@mail.com";
        when(service.getPatientByEmail(email)).thenThrow(new PatientNotFoundException(email));

        mockMvc.perform(get("/api/patients/{email}", email))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient not found: " + email));
    }

    @Test
    void createPatient_ValidData_ReturnsCreatedPatient() throws Exception {
        CreatePatientDto dto = new CreatePatientDto(1L, "abc123", "Ania", "Kowalska", "123456789", LocalDate.of(2000, 12, 1));
        Patient patient = Patient.from(dto, new User("ania@mail.com", "123"));
        when(service.addPatient(dto)).thenReturn(patient);

        mockMvc.perform(post("/api/patients").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("ania@mail.com"))
                .andExpect(jsonPath("$.firstName").value("Ania"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void createPatient_EmailExists_ReturnsConflict() throws Exception {
        CreatePatientDto dto = new CreatePatientDto(1L, "abc123", "Ania", "Kowalska", "123456789", LocalDate.of(2000, 12, 1));
        when(service.addPatient(dto)).thenThrow(new PatientAlreadyExistsException("ania@mail.com"));

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Patient already exists: " + "ania@mail.com"));
    }

    @Test
    void deletePatient_PatientExists_ReturnsNoContent() throws Exception {
        String email = "ania@mail.com";

        mockMvc.perform(delete("/api/patients/{email}", email))
                .andExpect(status().isNoContent());
        verify(service).deletePatientByEmail(email);
    }

    @Test
    void updatePatient_PatientExists_ReturnsUpdatedPatient() throws Exception {
        String email = "ania@mail.com";
        UpdatePatientDto dto = new UpdatePatientDto("Anna", "Nowak", "987654321", LocalDate.of(2001, 1, 2));
        Patient updatedPatient = new Patient(new User(email, "123"), "abc123", "Anna", "Nowak", "987654321", LocalDate.of(2001, 1, 2));
        when(service.updatePatientByEmail(email, dto)).thenReturn(updatedPatient);

        mockMvc.perform(put("/api/patients/{email}", email).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.idCardNo").value("abc123"))
                .andExpect(jsonPath("$.firstName").value("Anna"))
                .andExpect(jsonPath("$.lastName").value("Nowak"))
                .andExpect(jsonPath("$.birthday").value("2001-01-02"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }
}

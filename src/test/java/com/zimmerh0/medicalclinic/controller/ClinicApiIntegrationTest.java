package com.zimmerh0.medicalclinic.controller;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ClinicApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EntityManager entityManager;

    @Test
    void userCrudPreservesStatusesAndEmailUniqueness() throws Exception {
        long userId = createUser("anna@mail.com");
        createUser("other@mail.com");

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("anna@mail.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"anna@mail.com","password":"changed"}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"other@mail.com","password":"changed"}
                                """))
                .andExpect(status().isConflict());

        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"new@mail.com","password":"changed"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@mail.com"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"other@mail.com","password":"123"}
                                """))
                .andExpect(status().isConflict());

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void doctorCreatesVisitAndPatientBooksIt() throws Exception {
        long doctorUserId = createUser("doctor@mail.com");
        long doctorId = createDoctor(doctorUserId);
        long facilityId = createFacility("Clinic");
        long secondFacilityId = createFacility("Second clinic");
        long patientUserId = createUser("patient@mail.com");
        createPatient(patientUserId);
        long patientId = jdbcTemplate.queryForObject(
                "select id from patients where user_id = ?", Long.class, patientUserId);

        mockMvc.perform(put("/api/doctors/{doctorId}/facilities/{facilityId}", doctorId, facilityId))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/doctors/{id}", doctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.facilities[0].id").value(facilityId));
        mockMvc.perform(get("/api/facilities/{id}", facilityId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Clinic"));

        String visitBody = visitBody(doctorId, facilityId, "2099-01-01T10:00:00", "2099-01-01T10:45:00");
        long visitId = create("/api/visits", visitBody);
        assertEquals(0, jdbcTemplate.queryForObject(
                "select count(*) from visits where id = ? and patient_id is not null", Integer.class, visitId));

        mockMvc.perform(put("/api/visits/{visitId}/book", visitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"patientId\":" + patientId + "}"))
                .andExpect(status().isNoContent());
        entityManager.flush();
        assertEquals(patientId, jdbcTemplate.queryForObject(
                "select patient_id from visits where id = ?", Long.class, visitId));

        mockMvc.perform(put("/api/visits/{visitId}/book", visitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"patientId\":" + patientId + "}"))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(visitBody(doctorId, secondFacilityId, "2099-01-01T10:15:00", "2099-01-01T11:00:00")))
                .andExpect(status().isConflict());

        create("/api/visits", visitBody(doctorId, facilityId, "2099-01-01T10:45:00", "2099-01-01T11:00:00"));

        mockMvc.perform(delete("/api/users/{id}", patientUserId))
                .andExpect(status().isConflict());
        mockMvc.perform(delete("/api/users/{id}", doctorUserId))
                .andExpect(status().isConflict());
    }

    @ParameterizedTest
    @CsvSource({
            "2000-01-01T10:00:00, 2000-01-01T10:45:00",
            "2099-01-01T10:00:00, 2099-01-01T10:00:00",
            "2099-01-01T10:00:00, 2099-01-01T09:45:00",
            "2099-01-01T10:01:00, 2099-01-01T10:45:00",
            "2099-01-01T10:00:00, 2099-01-01T10:46:00",
            "2099-01-01T10:00:01, 2099-01-01T10:45:00",
            "2099-01-01T10:00:00, 2099-01-01T10:45:00.000000001"
    })
    void invalidVisitDatesAreStillRejectedByService(String startDate, String endDate) throws Exception {
        long doctorId = createDoctor(createUser("doctor@mail.com"));
        long facilityId = createFacility("Clinic");

        mockMvc.perform(post("/api/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(visitBody(doctorId, facilityId, startDate, endDate)))
                .andExpect(status().isBadRequest());
        assertEquals(0, jdbcTemplate.queryForObject("select count(*) from visits", Integer.class));
    }

    @Test
    void deletingPatientReturnsNoContentAndKeepsUser() throws Exception {
        long userId = createUser("patient@mail.com");
        createPatient(userId);

        mockMvc.perform(delete("/api/patients/patient@mail.com"))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/patients/patient@mail.com"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk());
    }

    private long createUser(String email) throws Exception {
        return create("/api/users", "{\"email\":\"" + email + "\",\"password\":\"123\"}");
    }

    private long createDoctor(long userId) throws Exception {
        return create("/api/doctors", """
                {"userId":%d,"firstName":"Jan","lastName":"Nowak","specialization":"Dentist"}
                """.formatted(userId));
    }

    private long createFacility(String name) throws Exception {
        return create("/api/facilities", """
                {"name":"%s","city":"Warsaw","postalCode":"00-001","street":"Test","buildingNumber":"1"}
                """.formatted(name));
    }

    private void createPatient(long userId) throws Exception {
        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId":%d,"idCardNo":"ABC123","firstName":"Anna","lastName":"Nowak","phoneNumber":"123456789","birthday":"2000-01-02"}
                                """.formatted(userId)))
                .andExpect(status().isCreated());
    }

    private String visitBody(long doctorId, long facilityId, String startDate, String endDate) {
        return """
                {"doctorId":%d,"facilityId":%d,"startDate":"%s","endDate":"%s"}
                """.formatted(doctorId, facilityId, startDate, endDate);
    }

    private long create(String path, String body) throws Exception {
        String response = mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).path("id").asLong();
    }
}

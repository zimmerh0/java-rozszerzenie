package com.zimmerh0.medicalclinic.controller;

import com.zimmerh0.medicalclinic.service.DoctorService;
import com.zimmerh0.medicalclinic.service.FacilityService;
import com.zimmerh0.medicalclinic.service.PatientService;
import com.zimmerh0.medicalclinic.service.UserService;
import com.zimmerh0.medicalclinic.service.VisitService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private PatientService patientService;
    @MockitoBean
    private DoctorService doctorService;
    @MockitoBean
    private FacilityService facilityService;
    @MockitoBean
    private VisitService visitService;

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void invalidInputReturnsBadRequestBeforeCallingService(String method, String path, String body) throws Exception {
        mockMvc.perform(request(HttpMethod.valueOf(method), path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService, patientService, doctorService, facilityService, visitService);
    }

    static Stream<Arguments> invalidRequests() {
        return Stream.of(
                Arguments.of("POST", "/api/users", """
                        {"email":"invalid-email","password":"123"}
                        """),
                Arguments.of("POST", "/api/users", """
                        {"email":"anna@mail.com","password":" "}
                        """),
                Arguments.of("PUT", "/api/users/1", "{}"),
                Arguments.of("POST", "/api/doctors", """
                        {"userId":1,"firstName":"Anna","lastName":"Nowak","specialization":" "}
                        """),
                Arguments.of("POST", "/api/doctors", """
                        {"userId":0,"firstName":"Anna","lastName":"Nowak","specialization":"Dentist"}
                        """),
                Arguments.of("PUT", "/api/doctors/1/facilities/0", ""),
                Arguments.of("POST", "/api/facilities", """
                        {"name":"Clinic","city":" ","postalCode":"00-001","street":"Test","buildingNumber":"1"}
                        """),
                Arguments.of("POST", "/api/patients", """
                        {"userId":null,"idCardNo":"ABC123","firstName":"Anna","lastName":"Nowak","phoneNumber":"123456789","birthday":"2000-01-02"}
                        """),
                Arguments.of("POST", "/api/visits", "{}"),
                Arguments.of("POST", "/api/visits", """
                        {"doctorId":0,"facilityId":1,"startDate":"2099-01-01T10:00:00","endDate":"2099-01-01T10:30:00"}
                        """),
                Arguments.of("POST", "/api/visits", """
                        {"doctorId":1,"facilityId":1,"startDate":"2099-01-01T10:00:00"}
                        """),
                Arguments.of("PUT", "/api/visits/1/book", "{}"),
                Arguments.of("PUT", "/api/visits/1/book", """
                        {"patientId":-1}
                        """),
                Arguments.of("PUT", "/api/patients/anna@mail.com/password", """
                        {"currentPassword":"123","newPassword":" "}
                        """)
        );
    }
}

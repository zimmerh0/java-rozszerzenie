package com.zimmerh0.medicalclinic.controller;

import com.zimmerh0.medicalclinic.dto.facility.CreateFacilityDto;
import com.zimmerh0.medicalclinic.facility.Facility;
import com.zimmerh0.medicalclinic.repository.FacilityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class FacilityControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private FacilityRepository facilityRepository;

    @Test
    void getFacilities_NoParameters_DefaultPageReturned() throws Exception {
        //given
        for (int i = 1; i <= 21; i++) {
            saveFacility("Placowka " + i);
        }

        //when + then
        mockMvc.perform(get("/api/facilities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(20))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(21))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    void getFacilities_SecondPageRequested_CorrectFacilityAndMetadataReturned() throws Exception {
        //given
        saveFacility("Gamma");
        saveFacility("Alfa");
        Facility beta = saveFacility("Beta");

        //when + then
        mockMvc.perform(get("/api/facilities")
                        .param("page", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(beta.getId()))
                .andExpect(jsonPath("$.content[0].name").value("Beta"))
                .andExpect(jsonPath("$.content[0].city").value("Warszawa"))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(3));
    }

    @Test
    void getFacilities_FacilitiesMissing_EmptyPageReturned() throws Exception {
        //when + then
        mockMvc.perform(get("/api/facilities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));
    }

    @Test
    void getFacilities_UnsortedFacilities_NamesSortedAscending() throws Exception {
        //given
        saveFacility("Gamma");
        saveFacility("Alfa");
        saveFacility("Beta");

        //when + then
        mockMvc.perform(get("/api/facilities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content[0].name").value("Alfa"))
                .andExpect(jsonPath("$.content[1].name").value("Beta"))
                .andExpect(jsonPath("$.content[2].name").value("Gamma"));
    }

    @Test
    void getFacilities_NegativePage_ReturnsBadRequest() throws Exception {
        //when + then
        mockMvc.perform(get("/api/facilities").param("page", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getFacilities_ZeroSize_ReturnsBadRequest() throws Exception {
        //when + then
        mockMvc.perform(get("/api/facilities").param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getFacilities_SizeAboveLimit_ReturnsBadRequest() throws Exception {
        //when + then
        mockMvc.perform(get("/api/facilities").param("size", "101"))
                .andExpect(status().isBadRequest());
    }

    private Facility saveFacility(String name) {
        return facilityRepository.save(Facility.from(new CreateFacilityDto(
                name, "Warszawa", "00-001", "Polna", "10")));
    }
}

package com.zimmerh0.medicalclinic.service;

import com.zimmerh0.medicalclinic.dto.facility.CreateFacilityDto;
import com.zimmerh0.medicalclinic.exception.FacilityAlreadyExistsException;
import com.zimmerh0.medicalclinic.exception.FacilityNotFoundException;
import com.zimmerh0.medicalclinic.facility.Facility;
import com.zimmerh0.medicalclinic.repository.FacilityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FacilityServiceTest {

    private FacilityService facilityService;
    private FacilityRepository facilityRepository;

    @BeforeEach
    void setUp() {
        facilityRepository = Mockito.mock(FacilityRepository.class);
        facilityService = new FacilityService(facilityRepository);
    }

    @Test
    void addFacility_NameExists_ThrowsException() {
        //given
        CreateFacilityDto dto = new CreateFacilityDto(
                "Przychodnia Centrum", "Warszawa", "00-001", "Polna", "10");
        when(facilityRepository.existsByName(dto.name())).thenReturn(true);

        //when
        assertThrows(FacilityAlreadyExistsException.class, () -> facilityService.addFacility(dto));

        //then
        verify(facilityRepository, never()).save(any(Facility.class));
    }

    @Test
    void getFacilityById_FacilityMissing_ThrowsException() {
        //given
        Long id = 1L;
        when(facilityRepository.findById(id)).thenReturn(Optional.empty());

        //when
        FacilityNotFoundException exception = assertThrows(FacilityNotFoundException.class,
                () -> facilityService.getFacilityById(id));

        //then
        assertEquals("Facility not found: " + id, exception.getMessage());
    }
}

package com.zimmerh0.medicalclinic.service;

import com.zimmerh0.medicalclinic.doctor.Doctor;
import com.zimmerh0.medicalclinic.dto.doctor.CreateDoctorDto;
import com.zimmerh0.medicalclinic.dto.facility.CreateFacilityDto;
import com.zimmerh0.medicalclinic.dto.user.UserRequest;
import com.zimmerh0.medicalclinic.facility.Facility;
import com.zimmerh0.medicalclinic.repository.DoctorRepository;
import com.zimmerh0.medicalclinic.repository.FacilityRepository;
import com.zimmerh0.medicalclinic.repository.UserRepository;
import com.zimmerh0.medicalclinic.user.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doAnswer;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:medicalclinic-transaction-tests;DB_CLOSE_ON_EXIT=FALSE")
public class TransactionIntegrationTest {

    @MockitoSpyBean
    private UserService userService;
    @MockitoSpyBean
    private DoctorService doctorService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private FacilityRepository facilityRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void cleanDatabase() {
        doctorRepository.deleteAll();
        facilityRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void updateUserById_ErrorAfterDatabaseUpdate_OriginalDataRestored() {
        //given
        User user = userRepository.save(new User("original@mail.com", "originalPassword"));
        UserRequest request = new UserRequest("changed@mail.com", "changedPassword");

        doAnswer(invocation -> {
            invocation.callRealMethod();
            entityManager.flush();
            assertEquals("changed@mail.com", jdbcTemplate.queryForObject(
                    "select email from users where id = ?", String.class, user.getId()));
            assertEquals("changedPassword", jdbcTemplate.queryForObject(
                    "select password from users where id = ?", String.class, user.getId()));
            throw new IllegalStateException("Error after updating user");
        }).when(userService).updateUserById(user.getId(), request);

        //when
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> userService.updateUserById(user.getId(), request));

        //then
        assertEquals("Error after updating user", exception.getMessage());
        assertAll(
                () -> assertEquals("original@mail.com", jdbcTemplate.queryForObject(
                        "select email from users where id = ?", String.class, user.getId())),
                () -> assertEquals("originalPassword", jdbcTemplate.queryForObject(
                        "select password from users where id = ?", String.class, user.getId()))
        );
    }

    @Test
    void assignFacility_ErrorAfterDatabaseInsert_NewRelationRolledBack() {
        //given
        User user = userRepository.save(new User("doctor@mail.com", "123"));
        Doctor doctor = doctorRepository.save(Doctor.from(
                new CreateDoctorDto(user.getId(), "Jan", "Nowak", "Kardiolog"), user));
        Facility existingFacility = saveFacility("Existing clinic");
        Facility newFacility = saveFacility("New clinic");
        doctorService.assignFacility(doctor.getId(), existingFacility.getId());

        doAnswer(invocation -> {
            invocation.callRealMethod();
            entityManager.flush();
            assertEquals(1, relationCount(doctor.getId(), newFacility.getId()));
            throw new IllegalStateException("Error after assigning facility");
        }).when(doctorService).assignFacility(doctor.getId(), newFacility.getId());

        //when
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> doctorService.assignFacility(doctor.getId(), newFacility.getId()));

        //then
        assertEquals("Error after assigning facility", exception.getMessage());
        assertAll(
                () -> assertEquals(0, relationCount(doctor.getId(), newFacility.getId())),
                () -> assertEquals(1, relationCount(doctor.getId(), existingFacility.getId())),
                () -> assertTrue(doctorRepository.existsById(doctor.getId())),
                () -> assertTrue(facilityRepository.existsById(newFacility.getId()))
        );
    }

    private Facility saveFacility(String name) {
        return facilityRepository.save(Facility.from(new CreateFacilityDto(
                name, "Warszawa", "00-001", "Polna", "10")));
    }

    private int relationCount(Long doctorId, Long facilityId) {
        return jdbcTemplate.queryForObject(
                "select count(*) from doctor_facilities where doctor_id = ? and facility_id = ?",
                Integer.class, doctorId, facilityId);
    }
}

package com.zimmerh0.medicalclinic.service;

import com.zimmerh0.medicalclinic.dto.user.UserRequest;
import com.zimmerh0.medicalclinic.exception.UserNotFoundException;
import com.zimmerh0.medicalclinic.repository.DoctorRepository;
import com.zimmerh0.medicalclinic.repository.PatientRepository;
import com.zimmerh0.medicalclinic.repository.UserRepository;
import com.zimmerh0.medicalclinic.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;
    private PatientRepository patientRepository;
    private DoctorRepository doctorRepository;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        patientRepository = Mockito.mock(PatientRepository.class);
        doctorRepository = Mockito.mock(DoctorRepository.class);
        userService = new UserService(userRepository, patientRepository, doctorRepository);
    }

    @Test
    void getAllUsers_UsersExist_UsersReturned() {
        //given
        User user1 = new User("anna@mail.com", "123");
        User user2 = new User("jan@mail.com", "456");
        Pageable pageable = PageRequest.of(0, 20);
        when(userRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(user1, user2), pageable, 2));

        //when
        Page<User> result = userService.getAllUsers(pageable);

        //then
        assertAll(
                () -> assertEquals(2, result.getContent().size()),
                () -> assertEquals("anna@mail.com", result.getContent().get(0).getEmail()),
                () -> assertEquals("jan@mail.com", result.getContent().get(1).getEmail())
        );
        verify(userRepository).findAll(pageable);
    }

    @Test
    void getAllUsers_UsersMissing_EmptyPageReturned() {
        //given
        Pageable pageable = PageRequest.of(0, 20);
        when(userRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(), pageable, 0));

        //when
        Page<User> result = userService.getAllUsers(pageable);

        //then
        assertTrue(result.isEmpty());
        verify(userRepository).findAll(pageable);
    }

    @Test
    void updateUserById_UserMissing_ThrowsException() {
        //given
        Long id = 1L;
        UserRequest request = new UserRequest("anna@mail.com", "newPassword");
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        //when
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.updateUserById(id, request));

        //then
        assertEquals("User not found: " + id, exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUserById_UserMissing_ThrowsException() {
        //given
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        //when
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.deleteUserById(id));

        //then
        assertEquals("User not found: " + id, exception.getMessage());
        verify(userRepository, never()).delete(any(User.class));
        verifyNoInteractions(patientRepository, doctorRepository);
    }
}

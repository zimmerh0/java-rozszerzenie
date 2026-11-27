package com.zimmerh0.medicalclinic.service;

import com.zimmerh0.medicalclinic.dto.user.UserRequest;
import com.zimmerh0.medicalclinic.exception.UserAlreadyExistsException;
import com.zimmerh0.medicalclinic.exception.UserNotFoundException;
import com.zimmerh0.medicalclinic.repository.DoctorRepository;
import com.zimmerh0.medicalclinic.repository.PatientRepository;
import com.zimmerh0.medicalclinic.repository.UserRepository;
import com.zimmerh0.medicalclinic.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional
    public User addUser(UserRequest request) {
        validateEmailAvailable(request.email());

        User user = new User(request.email(), request.password());
        return userRepository.save(user);
    }

    @Transactional
    public User updateUserById(Long id, UserRequest request) {
        User user = getUserById(id);

        if (!user.getEmail().equals(request.email())) {
            validateEmailAvailable(request.email());
        }

        user.update(request);
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUserById(Long id) {
        User user = getUserById(id);

        if (patientRepository.existsByUser_Id(id) || doctorRepository.existsByUserId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User has a patient or doctor profile");
        }

        userRepository.delete(user);
    }

    private void validateEmailAvailable(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(email);
        }
    }
}

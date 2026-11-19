package com.zimmerh0.medicalclinic.service;

import com.zimmerh0.medicalclinic.dto.user.UserRequest;
import com.zimmerh0.medicalclinic.exception.UserAlreadyExistsException;
import com.zimmerh0.medicalclinic.exception.UserNotFoundException;
import com.zimmerh0.medicalclinic.repository.PatientRepository;
import com.zimmerh0.medicalclinic.repository.UserRepository;
import com.zimmerh0.medicalclinic.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    public User addUser(UserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException(request.email());
        }

        User user = new User(request.email(), request.password());
        return userRepository.save(user);
    }

    public User updateUserById(Long id, UserRequest request) {
        User user = getUserById(id);

        if (!user.getEmail().equals(request.email()) && userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException(request.email());
        }

        user.update(request);
        return userRepository.save(user);
    }

    public boolean deleteUserById(Long id) {
        User user = getUserById(id);

        if (patientRepository.existsByUser_Id(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is linked to a patient");
        }

        userRepository.delete(user);
        return true;
    }
}
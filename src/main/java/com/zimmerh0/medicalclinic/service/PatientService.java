package com.zimmerh0.medicalclinic.service;

import com.zimmerh0.medicalclinic.dto.patient.ChangePasswordCommand;
import com.zimmerh0.medicalclinic.dto.patient.CreatePatientDto;
import com.zimmerh0.medicalclinic.dto.patient.UpdatePatientDto;
import com.zimmerh0.medicalclinic.exception.PatientAlreadyExistsException;
import com.zimmerh0.medicalclinic.exception.PatientNotFoundException;
import com.zimmerh0.medicalclinic.exception.UserNotFoundException;
import com.zimmerh0.medicalclinic.patient.Patient;
import com.zimmerh0.medicalclinic.repository.PatientRepository;
import com.zimmerh0.medicalclinic.repository.UserRepository;
import com.zimmerh0.medicalclinic.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient getPatientByEmail(String email) {
        return patientRepository.findByUser_Email(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
    }

    @Transactional
    public Patient addPatient(CreatePatientDto dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new UserNotFoundException(dto.userId()));

        if (patientRepository.existsByUser_Id(user.getId())) {
            throw new PatientAlreadyExistsException(user.getEmail());
        }

        return patientRepository.save(Patient.from(dto, user));
    }

    public void deletePatientByEmail(String email) {
        Patient patient = getPatientByEmail(email);
        patientRepository.delete(patient);
    }

    public Patient updatePatientByEmail(String email, UpdatePatientDto dto) {
        Patient patient = getPatientByEmail(email);
        patient.update(dto);
        return patientRepository.save(patient);
    }

    @Transactional
    public ChangePasswordResult changePassword(
            String email, ChangePasswordCommand command) {
        Optional<Patient> found = patientRepository.findByUser_Email(email);

        if (found.isEmpty()) {
            return ChangePasswordResult.PATIENT_NOT_FOUND;
        }

        User user = found.get().getUser();

        if (!user.getPassword().equals(command.currentPassword())) {
            return ChangePasswordResult.WRONG_PASSWORD;
        }

        user.changePassword(command.newPassword());
        userRepository.save(user);
        return ChangePasswordResult.CHANGED;
    }
}

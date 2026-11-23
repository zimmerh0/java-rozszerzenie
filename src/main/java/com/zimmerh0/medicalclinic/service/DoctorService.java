package com.zimmerh0.medicalclinic.service;

import com.zimmerh0.medicalclinic.doctor.Doctor;
import com.zimmerh0.medicalclinic.dto.doctor.CreateDoctorDto;
import com.zimmerh0.medicalclinic.exception.DoctorAlreadyExistsException;
import com.zimmerh0.medicalclinic.exception.DoctorNotFoundException;
import com.zimmerh0.medicalclinic.exception.UserNotFoundException;
import com.zimmerh0.medicalclinic.facility.Facility;
import com.zimmerh0.medicalclinic.repository.DoctorRepository;
import com.zimmerh0.medicalclinic.repository.UserRepository;
import com.zimmerh0.medicalclinic.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final FacilityService facilityService;

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));
    }

    public Doctor addDoctor(CreateDoctorDto dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new UserNotFoundException(dto.userId()));

        if (doctorRepository.existsByUserId(user.getId())) {
            throw new DoctorAlreadyExistsException(user.getId());
        }

        return doctorRepository.save(Doctor.from(dto, user));
    }

    @Transactional
    public void assignFacility(Long doctorId, Long facilityId) {
        Doctor doctor = getDoctorById(doctorId);
        Facility facility = facilityService.getFacilityById(facilityId);

        doctor.addFacility(facility);
        doctorRepository.save(doctor);
    }
}

package com.zimmerh0.medicalclinic.doctor;

import com.zimmerh0.medicalclinic.dto.doctor.CreateDoctorDto;
import com.zimmerh0.medicalclinic.facility.Facility;
import com.zimmerh0.medicalclinic.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "doctors")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String firstName;
    private String lastName;
    private String specialization;

    @ManyToMany
    @JoinTable(name = "doctor_facilities", joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "facility_id"))
    private Set<Facility> facilities = new HashSet<>();

    public static Doctor from(CreateDoctorDto dto, User user) {
        Doctor doctor = new Doctor();
        doctor.user = user;
        doctor.firstName = dto.firstName();
        doctor.lastName = dto.lastName();
        doctor.specialization = dto.specialization();
        return doctor;
    }

    public void addFacility(Facility facility) {
        facilities.add(facility);
        facility.getDoctors().add(this);
    }
}
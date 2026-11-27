package com.zimmerh0.medicalclinic.patient;

import com.zimmerh0.medicalclinic.dto.patient.CreatePatientDto;
import com.zimmerh0.medicalclinic.dto.patient.UpdatePatientDto;
import com.zimmerh0.medicalclinic.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "patients")
@Getter
@Setter
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Setter(AccessLevel.NONE)
    private String idCardNo;

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate birthday;

    protected Patient() {
    }

    public Patient(User user, String idCardNo, String firstName,
                   String lastName, String phoneNumber, LocalDate birthday) {
        this.user = user;
        this.idCardNo = idCardNo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.birthday = birthday;
    }

    public static Patient from(CreatePatientDto dto, User user) {
        return new Patient(
                user,
                dto.idCardNo(),
                dto.firstName(),
                dto.lastName(),
                dto.phoneNumber(),
                dto.birthday()
        );
    }

    public void update(UpdatePatientDto dto) {
        firstName = dto.firstName();
        lastName = dto.lastName();
        phoneNumber = dto.phoneNumber();
        birthday = dto.birthday();
    }
}
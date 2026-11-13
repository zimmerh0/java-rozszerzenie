package com.zimmerh0.medicalclinic.patient;

import com.zimmerh0.medicalclinic.dto.CreatePatientDto;
import com.zimmerh0.medicalclinic.dto.UpdatePatientDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    @Setter(AccessLevel.NONE)
    private String idCardNo;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate birthday;

    protected Patient() {
    }

    public Patient(String email, String password, String idCardNo,
                   String firstName, String lastName, String phoneNumber,
                   LocalDate birthday) {
        this.email = email;
        this.password = password;
        this.idCardNo = idCardNo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.birthday = birthday;
    }

    public static Patient from(CreatePatientDto createPatientDto) {
        return new Patient(
                createPatientDto.email(),
                createPatientDto.password(),
                createPatientDto.idCardNo(),
                createPatientDto.firstName(),
                createPatientDto.lastName(),
                createPatientDto.phoneNumber(),
                createPatientDto.birthday()
        );
    }

    public void update(UpdatePatientDto updatePatientDto) {
        firstName = updatePatientDto.firstName();
        lastName = updatePatientDto.lastName();
        phoneNumber = updatePatientDto.phoneNumber();
        birthday = updatePatientDto.birthday();
    }

}

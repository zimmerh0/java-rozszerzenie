package com.zimmerh0.medicalclinic.patient;

import com.zimmerh0.medicalclinic.dto.CreatePatientDto;
import com.zimmerh0.medicalclinic.dto.UpdatePatientDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class Patient {
    private String email;
    private String password;
    private String idCardNo;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate birthday;

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
        //password = updatePatientDto.password();
        idCardNo = updatePatientDto.idCardNo();
        firstName = updatePatientDto.firstName();
        lastName = updatePatientDto.lastName();
        phoneNumber = updatePatientDto.phoneNumber();
        birthday = updatePatientDto.birthday();
    }
}

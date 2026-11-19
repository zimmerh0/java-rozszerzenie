package com.zimmerh0.medicalclinic.facility;

import com.zimmerh0.medicalclinic.doctor.Doctor;
import com.zimmerh0.medicalclinic.dto.facility.CreateFacilityDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "facilities")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Facility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String city;
    private String postalCode;
    private String street;
    private String buildingNumber;

    @ManyToMany(mappedBy = "facilities")
    private Set<Doctor> doctors = new HashSet<>();

    public static Facility from(CreateFacilityDto dto) {
        Facility facility = new Facility();
        facility.name = dto.name();
        facility.city = dto.city();
        facility.postalCode = dto.postalCode();
        facility.street = dto.street();
        facility.buildingNumber = dto.buildingNumber();
        return facility;
    }
}
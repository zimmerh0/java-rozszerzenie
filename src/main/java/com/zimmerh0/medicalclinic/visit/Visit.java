package com.zimmerh0.medicalclinic.visit;

import com.zimmerh0.medicalclinic.doctor.Doctor;
import com.zimmerh0.medicalclinic.facility.Facility;
import com.zimmerh0.medicalclinic.patient.Patient;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "visits", uniqueConstraints = @UniqueConstraint(columnNames = {"doctor_id", "start_date"}))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @NonNull
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @NonNull
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;
}
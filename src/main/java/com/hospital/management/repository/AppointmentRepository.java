package com.hospital.management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.management.model.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByStatus(String status);

    boolean existsByDoctorIdAndAppointmentDateAndStatusNot(
            Long doctorId,
            String appointmentDate,
            String status
    );

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorId(Long doctorId);

    Optional<Appointment> findByAppointmentIdAndPatientId(
            Long appointmentId,
            Long patientId
    );
}

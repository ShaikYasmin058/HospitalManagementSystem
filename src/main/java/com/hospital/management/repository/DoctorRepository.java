package com.hospital.management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.management.model.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findByNameContainingIgnoreCaseOrSpecializationContainingIgnoreCase(
            String name,
            String specialization
    );

    Optional<Doctor> findByAccountId(Long accountId);
}

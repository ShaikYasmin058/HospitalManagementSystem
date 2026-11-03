package com.hospital.management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.management.model.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    List<Patient> findByNameContainingIgnoreCase(String name);

    Optional<Patient> findByAccountId(Long accountId);
}

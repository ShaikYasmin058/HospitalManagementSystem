package com.hospital.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.management.model.MedicalRecord;

public interface MedicalRecordRepository
        extends JpaRepository<MedicalRecord, Long> {

    // Patient's medical history
    List<MedicalRecord> findByPatientIdOrderByRecordDateDesc(Long patientId);

    // All medical records created by a particular doctor
    List<MedicalRecord> findByDoctorIdOrderByRecordDateDesc(Long doctorId);

    // Records created by a particular doctor for a particular patient
    List<MedicalRecord> findByPatientIdAndDoctorIdOrderByRecordDateDesc(
            Long patientId, Long doctorId);
}


package com.hospital.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.management.model.Bill;

public interface BillRepository extends JpaRepository<Bill, Long> {

    List<Bill> findByPatientIdOrderByBillDateDesc(Long patientId);
}

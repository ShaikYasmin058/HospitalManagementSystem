
package com.hospital.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.management.model.Staff;

public interface StaffRepository extends JpaRepository<Staff, Long> {

    List<Staff> findByNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase(
            String name, String department);
}

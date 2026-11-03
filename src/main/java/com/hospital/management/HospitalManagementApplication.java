package com.hospital.management;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hospital.management.model.Admin;
import com.hospital.management.model.Role;
import com.hospital.management.repository.AdminRepository;

@SpringBootApplication
public class HospitalManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(HospitalManagementApplication.class, args);
    }

    @Bean
    ApplicationRunner initAdmin(
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            var adminOptional = adminRepository.findByUsername("admin");

            if (adminOptional.isEmpty()) {

                Admin admin = new Admin();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setRole(Role.ADMIN);

                adminRepository.save(admin);

            } else {

                Admin admin = adminOptional.get();

                // Upgrade an existing plain-text password to BCrypt
                if (admin.getPassword() != null
                        && !admin.getPassword().startsWith("$2")) {

                    admin.setPassword(
                            passwordEncoder.encode(admin.getPassword()));
                }

                // Assign ADMIN role if the existing account has no role
                if (admin.getRole() == null) {
                    admin.setRole(Role.ADMIN);
                }

                adminRepository.save(admin);
            }

            // Ensure every existing account has a role
            for (Admin account : adminRepository.findAll()) {
                if (account.getRole() == null) {
                    account.setRole(Role.ADMIN);
                    adminRepository.save(account);
                }
            }
        };
    }
}
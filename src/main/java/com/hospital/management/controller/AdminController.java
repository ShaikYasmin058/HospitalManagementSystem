
package com.hospital.management.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.management.model.Appointment;
import com.hospital.management.model.Doctor;
import com.hospital.management.model.Patient;
import com.hospital.management.model.Role;
import com.hospital.management.repository.AdminRepository;
import com.hospital.management.service.HospitalService;

@Controller
public class AdminController {

    private final HospitalService hospitalService;
    private final AdminRepository adminRepository;
    private final ObjectMapper objectMapper;

    public AdminController(
            HospitalService hospitalService,
            AdminRepository adminRepository,
            ObjectMapper objectMapper) {

        this.hospitalService = hospitalService;
        this.adminRepository = adminRepository;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {

        List<Patient> patients = hospitalService.getAllPatients();
        List<Doctor> doctors = hospitalService.getAllDoctors();
        List<Appointment> appointments = hospitalService.getAllAppointments();

        // Dashboard statistics
        model.addAttribute("totalPatients", patients.size());
        model.addAttribute("totalDoctors", doctors.size());
        model.addAttribute("totalAppointments", appointments.size());

        // Count receptionist accounts as staff
        long totalStaff = adminRepository.findAll()
                .stream()
                .filter(admin -> admin.getRole() == Role.RECEPTIONIST)
                .count();

        model.addAttribute("totalStaff", totalStaff);

        // Today's appointments
        LocalDate today = LocalDate.now();

        long todayAppointments = appointments.stream()
                .filter(a -> {
                    LocalDateTime dateTime =
                            parseAppointmentDate(a.getAppointmentDate());

                    return dateTime != null
                            && dateTime.toLocalDate().equals(today);
                })
                .count();

        model.addAttribute("todayAppointments", todayAppointments);

        // Appointment status statistics
        long scheduled = countByStatus(appointments, "Scheduled");
        long completed = countByStatus(appointments, "Completed");
        long cancelled = countByStatus(appointments, "Cancelled");

        model.addAttribute("scheduledAppointments", scheduled);
        model.addAttribute("completedAppointments", completed);
        model.addAttribute("cancelledAppointments", cancelled);

        // Patient ID and name mapping
        Map<Long, String> patientNames = new HashMap<>();

        for (Patient patient : patients) {
            patientNames.put(
                    patient.getPatientId(),
                    patient.getName()
            );
        }

        model.addAttribute("patientNames", patientNames);

        // Doctor ID and name mapping
        Map<Long, String> doctorNames = new HashMap<>();

        for (Doctor doctor : doctors) {
            doctorNames.put(
                    doctor.getDoctorId(),
                    doctor.getName()
            );
        }

        model.addAttribute("doctorNames", doctorNames);

        // Latest five appointments by appointment ID
        List<Appointment> recentAppointments =
                new ArrayList<>(appointments);

        recentAppointments.sort(
                Comparator.comparing(
                        Appointment::getAppointmentId,
                        Comparator.nullsLast(Comparator.reverseOrder())
                )
        );

        if (recentAppointments.size() > 5) {
            recentAppointments =
                    recentAppointments.subList(0, 5);
        }

        model.addAttribute("recentAppointments", recentAppointments);

        // Daily appointment chart
        Map<LocalDate, Long> dailyCounts = new TreeMap<>();

        for (Appointment appointment : appointments) {

            LocalDateTime dateTime =
                    parseAppointmentDate(appointment.getAppointmentDate());

            if (dateTime != null) {
                dailyCounts.merge(
                        dateTime.toLocalDate(),
                        1L,
                        Long::sum
                );
            }
        }

        List<String> appointmentDateLabels = new ArrayList<>();
        List<Long> appointmentDateValues = new ArrayList<>();

        DateTimeFormatter dailyFormatter =
                DateTimeFormatter.ofPattern("dd MMM");

        dailyCounts.forEach((date, count) -> {
            appointmentDateLabels.add(
                    date.format(dailyFormatter)
            );
            appointmentDateValues.add(count);
        });

        // Monthly appointment chart
        Map<YearMonth, Long> monthlyCounts = new TreeMap<>();

        for (Appointment appointment : appointments) {

            LocalDateTime dateTime =
                    parseAppointmentDate(appointment.getAppointmentDate());

            if (dateTime != null) {
                YearMonth month = YearMonth.from(dateTime);

                monthlyCounts.merge(
                        month,
                        1L,
                        Long::sum
                );
            }
        }

        List<String> monthlyAppointmentLabels = new ArrayList<>();
        List<Long> monthlyAppointmentValues = new ArrayList<>();

        DateTimeFormatter monthlyFormatter =
                DateTimeFormatter.ofPattern("MMM yyyy");

        monthlyCounts.forEach((month, count) -> {
            monthlyAppointmentLabels.add(
                    month.format(monthlyFormatter)
            );
            monthlyAppointmentValues.add(count);
        });

        // Convert chart data to JSON
        try {
            model.addAttribute(
                    "appointmentDateLabels",
                    objectMapper.writeValueAsString(
                            appointmentDateLabels
                    )
            );

            model.addAttribute(
                    "appointmentDateValues",
                    objectMapper.writeValueAsString(
                            appointmentDateValues
                    )
            );

            model.addAttribute(
                    "monthlyAppointmentLabels",
                    objectMapper.writeValueAsString(
                            monthlyAppointmentLabels
                    )
            );

            model.addAttribute(
                    "monthlyAppointmentValues",
                    objectMapper.writeValueAsString(
                            monthlyAppointmentValues
                    )
            );

        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Unable to prepare dashboard chart data",
                    e
            );
        }

        return "dashboard";
    }

    // Count appointments by status
    private long countByStatus(
            List<Appointment> appointments,
            String expectedStatus) {

        return appointments.stream()
                .filter(a -> expectedStatus.equalsIgnoreCase(a.getStatus()))
                .count();
    }

    // Parse appointment date safely
    private LocalDateTime parseAppointmentDate(String date) {

        if (date == null || date.isBlank()) {
            return null;
        }

        try {
            return LocalDateTime.parse(date);
        } catch (Exception e) {
            try {
                return LocalDate.parse(date).atStartOfDay();
            } catch (Exception ignored) {
                return null;
            }
        }
    }
}

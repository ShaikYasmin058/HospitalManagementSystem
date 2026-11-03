
package com.hospital.management.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.hospital.management.model.Appointment;
import com.hospital.management.service.HospitalService;

@Controller
public class DashboardController {

    private final HospitalService hospitalService;
    private final com.hospital.management.repository.StaffRepository staffRepository;

    public DashboardController(
            HospitalService hospitalService,
            com.hospital.management.repository.StaffRepository staffRepository) {

        this.hospitalService = hospitalService;
        this.staffRepository = staffRepository;
    }

    // Home page only. AdminController handles /dashboard.
    @GetMapping("/")
    public String dashboard(Model model) {

        model.addAttribute("totalPatients", hospitalService.countPatients());
        model.addAttribute("totalDoctors", hospitalService.countDoctors());
        model.addAttribute("totalAppointments", hospitalService.countAppointments());
        model.addAttribute("totalStaff", staffRepository.count());

        List<Appointment> appointments = hospitalService.getAllAppointments();

        String today = java.time.LocalDate.now().toString();

        long todayAppointments = appointments.stream()
                .filter(a -> a.getAppointmentDate() != null
                        && a.getAppointmentDate().startsWith(today))
                .count();

        long scheduledAppointments = appointments.stream()
                .filter(a -> "Scheduled".equalsIgnoreCase(a.getStatus()))
                .count();

        long completedAppointments = appointments.stream()
                .filter(a -> "Completed".equalsIgnoreCase(a.getStatus()))
                .count();

        long cancelledAppointments = appointments.stream()
                .filter(a -> "Cancelled".equalsIgnoreCase(a.getStatus()))
                .count();

        model.addAttribute("todayAppointments", todayAppointments);
        model.addAttribute("scheduledAppointments", scheduledAppointments);
        model.addAttribute("completedAppointments", completedAppointments);
        model.addAttribute("cancelledAppointments", cancelledAppointments);

        // Monthly appointment analytics
        java.util.Map<String, Long> monthlyAppointmentCounts =
                appointments.stream()
                        .filter(a -> a.getAppointmentDate() != null
                                && a.getAppointmentDate().length() >= 7)
                        .collect(java.util.stream.Collectors.groupingBy(
                                a -> a.getAppointmentDate().substring(0, 7),
                                java.util.TreeMap::new,
                                java.util.stream.Collectors.counting()
                        ));

        model.addAttribute("monthlyAppointmentLabels",
                monthlyAppointmentCounts.keySet().stream()
                        .map(month -> "\"" + month + "\"")
                        .collect(java.util.stream.Collectors.joining(",", "[", "]")));

        model.addAttribute("monthlyAppointmentValues",
                monthlyAppointmentCounts.values().stream()
                        .map(String::valueOf)
                        .collect(java.util.stream.Collectors.joining(",", "[", "]")));

        // Daily appointment analytics
        java.util.Map<String, Long> appointmentDateCounts =
                appointments.stream()
                        .filter(a -> a.getAppointmentDate() != null
                                && a.getAppointmentDate().length() >= 10)
                        .collect(java.util.stream.Collectors.groupingBy(
                                a -> a.getAppointmentDate().substring(0, 10),
                                java.util.TreeMap::new,
                                java.util.stream.Collectors.counting()
                        ));

        model.addAttribute("appointmentDateLabels",
                appointmentDateCounts.keySet().stream()
                        .map(date -> "\"" + date + "\"")
                        .collect(java.util.stream.Collectors.joining(",", "[", "]")));

        model.addAttribute("appointmentDateValues",
                appointmentDateCounts.values().stream()
                        .map(String::valueOf)
                        .collect(java.util.stream.Collectors.joining(",", "[", "]")));

        // Patient and doctor names
        Map<Long, String> patientNames = new HashMap<>();
        Map<Long, String> doctorNames = new HashMap<>();

        for (Appointment appointment : appointments) {

            hospitalService.getPatientById(appointment.getPatientId())
                    .ifPresent(patient -> patientNames.put(
                            appointment.getPatientId(),
                            patient.getName()
                    ));

            hospitalService.getDoctorById(appointment.getDoctorId())
                    .ifPresent(doctor -> doctorNames.put(
                            appointment.getDoctorId(),
                            doctor.getName()
                    ));
        }

        model.addAttribute("recentAppointments", appointments);
        model.addAttribute("patientNames", patientNames);
        model.addAttribute("doctorNames", doctorNames);

        return "dashboard";
    }
}

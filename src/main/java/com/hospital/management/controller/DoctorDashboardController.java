package com.hospital.management.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.hospital.management.model.Doctor;
import com.hospital.management.model.Appointment;
import com.hospital.management.service.HospitalService;

import jakarta.servlet.http.HttpSession;

@Controller
public class DoctorDashboardController {

    private final HospitalService hospitalService;

    public DoctorDashboardController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @GetMapping("/doctor/dashboard")
    public String doctorDashboard(
            HttpSession session,
            Model model) {

        Long accountId = (Long) session.getAttribute("loggedInAdminId");

        Doctor doctor = hospitalService
                .getDoctorByAccountId(accountId)
                .orElse(null);

        if (doctor == null) {
            model.addAttribute(
                    "error",
                    "Your doctor profile is not linked to your account.");
            return "doctor-dashboard";
        }

        List<Appointment> appointments
                = hospitalService.getAppointmentsByDoctorId(
                        doctor.getDoctorId());

        model.addAttribute("doctor", doctor);
        model.addAttribute("appointments", appointments);

        return "doctor-dashboard";
    }

    @GetMapping("/doctor/appointments")
    public String doctorAppointments(
            HttpSession session,
            Model model) {

        Long accountId = (Long) session.getAttribute("loggedInAdminId");

        Doctor doctor = hospitalService
                .getDoctorByAccountId(accountId)
                .orElse(null);

        if (doctor == null) {
            model.addAttribute(
                    "error",
                    "Your doctor profile is not linked to your account.");
            return "doctor-appointments";
        }

        List<Appointment> appointments
                = hospitalService.getAppointmentsByDoctorId(
                        doctor.getDoctorId());
        System.out.println("DOCTOR ID: " + doctor.getDoctorId());

        for (Appointment appointment : appointments) {
            System.out.println("APPOINTMENT ID: " + appointment.getAppointmentId());
            System.out.println("PATIENT ID: " + appointment.getPatientId());
            System.out.println("STATUS: [" + appointment.getStatus() + "]");
        }

        model.addAttribute("doctor", doctor);
        model.addAttribute("appointments", appointments);

        return "doctor-appointments";
    }
}

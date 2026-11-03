package com.hospital.management.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hospital.management.model.Appointment;
import com.hospital.management.service.HospitalService;

import jakarta.validation.Valid;

@Controller
public class AppointmentController {

    private final HospitalService hospitalService;

    public AppointmentController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @GetMapping("/appointments")
    public String listAppointments(
            @RequestParam(defaultValue = "All") String status,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String date,
            Model model) {

        List<Appointment> allAppointments
                = hospitalService.getAllAppointments();

        List<Appointment> appointments = allAppointments.stream()
                .filter(appointment
                        -> "All".equalsIgnoreCase(status)
                        || status.equalsIgnoreCase(appointment.getStatus()))
                .filter(appointment -> {

                    if (keyword.isBlank()) {
                        return true;
                    }

                    String patientName = hospitalService
                            .getPatientById(appointment.getPatientId())
                            .map(patient -> patient.getName())
                            .orElse("");

                    String doctorName = hospitalService
                            .getDoctorById(appointment.getDoctorId())
                            .map(doctor -> doctor.getName())
                            .orElse("");

                    return patientName.toLowerCase()
                            .contains(keyword.toLowerCase())
                            || doctorName.toLowerCase()
                                    .contains(keyword.toLowerCase());
                })
                .filter(appointment
                        -> date.isBlank()
                        || (appointment.getAppointmentDate() != null
                        && appointment.getAppointmentDate().startsWith(date)))
                .collect(Collectors.toList());

        Map<Long, String> patientNames = new HashMap<>();
        Map<Long, String> doctorNames = new HashMap<>();

        for (Appointment appointment : appointments) {

            hospitalService.getPatientById(appointment.getPatientId())
                    .ifPresent(patient
                            -> patientNames.put(
                                    appointment.getPatientId(),
                                    patient.getName()));

            hospitalService.getDoctorById(appointment.getDoctorId())
                    .ifPresent(doctor
                            -> doctorNames.put(
                                    appointment.getDoctorId(),
                                    doctor.getName()));
        }

        model.addAttribute("appointments", appointments);
        model.addAttribute("patientNames", patientNames);
        model.addAttribute("doctorNames", doctorNames);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedDate", date);

        return "appointments";
    }

    @GetMapping("/appointments/new")
    public String showAppointmentForm(Model model) {
        model.addAttribute("appointment", new Appointment());
        return "appointment-form";
    }

    @PostMapping("/appointments")
    public String saveAppointment(
            @Valid @ModelAttribute Appointment appointment,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "appointment-form";
        }

        System.out.println("BOOKING TEST: Doctor ID = "
                + appointment.getDoctorId()
                + ", Date = " + appointment.getAppointmentDate());

        // Prevent double-booking
        boolean alreadyBooked = hospitalService.getAllAppointments()
                .stream()
                .anyMatch(existing
                        -> !Objects.equals(
                                existing.getAppointmentId(),
                                appointment.getAppointmentId())
                        && Objects.equals(
                                existing.getDoctorId(),
                                appointment.getDoctorId())
                        && Objects.equals(
                                existing.getAppointmentDate(),
                                appointment.getAppointmentDate())
                        && !"Cancelled".equalsIgnoreCase(
                                existing.getStatus()));

        if (alreadyBooked) {
            result.reject("appointmentConflict",
                    "This doctor already has an appointment at that time.");

            return "appointment-form";
        }

        if (appointment.getId() == null) {
            appointment.setStatus("Scheduled");
        }

        hospitalService.saveAppointment(appointment);

        return "redirect:/appointments";
    }

    @GetMapping("/appointments/edit/{id}")
    public String editAppointment(
            @PathVariable Long id,
            Model model) {

        Appointment appointment = hospitalService.getAppointmentById(id)
                .orElseThrow(()
                        -> new RuntimeException("Appointment not found"));

        model.addAttribute("appointment", appointment);

        return "appointment-form";
    }

    @GetMapping("/appointments/cancel/{id}")
    public String cancelAppointment(@PathVariable Long id) {
        hospitalService.cancelAppointment(id);
        return "redirect:/appointments";
    }

    @GetMapping("/appointments/complete/{id}")
    public String completeAppointment(@PathVariable Long id) {

        Appointment appointment = hospitalService.getAppointmentById(id)
                .orElseThrow(()
                        -> new RuntimeException("Appointment not found"));

        appointment.setStatus("Completed");
        hospitalService.saveAppointment(appointment);

        return "redirect:/appointments";
    }
}

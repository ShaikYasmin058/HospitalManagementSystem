
package com.hospital.management.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.hospital.management.model.Appointment;
import com.hospital.management.model.Doctor;
import com.hospital.management.model.Patient;
import com.hospital.management.service.HospitalService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class PatientDashboardController {

    private final HospitalService hospitalService;

    public PatientDashboardController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    // Patient Dashboard
    @GetMapping("/patient/dashboard")
    public String showPatientDashboard(
            HttpSession session,
            Model model) {

        Long accountId =
                (Long) session.getAttribute("loggedInAdminId");

        if (accountId == null) {
            return "redirect:/login";
        }

        Optional<Patient> patient =
                hospitalService.getPatientByAccountId(accountId);

        if (patient.isPresent()) {
            model.addAttribute("patient", patient.get());
        } else {
            model.addAttribute(
                    "message",
                    "Your patient profile has not been linked yet. Please contact the administrator.");
        }

        return "patient-dashboard";
    }

    // Patient Appointments
    @GetMapping("/patient/appointments")
    public String showPatientAppointments(
            HttpSession session,
            Model model) {

        Long accountId =
                (Long) session.getAttribute("loggedInAdminId");

        if (accountId == null) {
            return "redirect:/login";
        }

        Optional<Patient> patient =
                hospitalService.getPatientByAccountId(accountId);

        if (patient.isPresent()) {

            Patient loggedInPatient = patient.get();

            List<Appointment> appointments =
                    hospitalService.getAppointmentsByPatientId(
                            loggedInPatient.getPatientId());

            // Automatically expire past scheduled appointments
            LocalDateTime now = LocalDateTime.now();

            for (Appointment appointment : appointments) {

                if ("Scheduled".equalsIgnoreCase(appointment.getStatus())
                        && appointment.getAppointmentDate() != null) {

                    try {

                        LocalDateTime appointmentDateTime =
                                LocalDateTime.parse(
                                        appointment.getAppointmentDate());

                        if (!appointmentDateTime.isAfter(now)) {

                            appointment.setStatus("Expired");

                            hospitalService.saveAppointment(appointment);
                        }

                    } catch (Exception e) {

                        System.out.println(
                                "Could not check appointment date for ID: "
                                + appointment.getAppointmentId());
                    }
                }
            }

            model.addAttribute("appointments", appointments);

        } else {

            model.addAttribute(
                    "message",
                    "Your patient profile has not been linked yet.");

            model.addAttribute("appointments", List.of());
        }

        return "patient-appointments";
    }

    // Open Patient Profile Setup Page
    @GetMapping("/patient/profile/new")
    public String showPatientProfileForm(
            HttpSession session,
            Model model) {

        Long accountId =
                (Long) session.getAttribute("loggedInAdminId");

        if (accountId == null) {
            return "redirect:/login";
        }

        model.addAttribute("patient", new Patient());

        return "patient-profile-form";
    }

    // Open Edit Patient Profile Page
    @GetMapping("/patient/profile/edit")
    public String showEditPatientProfile(
            HttpSession session,
            Model model) {

        Long accountId =
                (Long) session.getAttribute("loggedInAdminId");

        if (accountId == null) {
            return "redirect:/login";
        }

        Optional<Patient> patient =
                hospitalService.getPatientByAccountId(accountId);

        if (patient.isEmpty()) {
            return "redirect:/patient/profile/new";
        }

        model.addAttribute("patient", patient.get());

        return "patient-profile-edit";
    }

    // Update Patient Profile
    @PostMapping("/patient/profile/update")
    public String updatePatientProfile(
            @ModelAttribute("patient") Patient updatedPatient,
            HttpSession session) {

        Long accountId =
                (Long) session.getAttribute("loggedInAdminId");

        if (accountId == null) {
            return "redirect:/login";
        }

        Optional<Patient> existingPatient =
                hospitalService.getPatientByAccountId(accountId);

        if (existingPatient.isEmpty()) {
            return "redirect:/patient/profile/new";
        }

        Patient patient = existingPatient.get();

        patient.setName(updatedPatient.getName());
        patient.setAge(updatedPatient.getAge());
        patient.setGender(updatedPatient.getGender());
        patient.setPhone(updatedPatient.getPhone());
        patient.setDisease(updatedPatient.getDisease());

        hospitalService.savePatient(patient);

        return "redirect:/patient/dashboard";
    }

    // Save Patient Profile
    @PostMapping("/patient/profile")
    public String savePatientProfile(
            @ModelAttribute("patient") Patient patient,
            HttpSession session) {

        Long accountId =
                (Long) session.getAttribute("loggedInAdminId");

        if (accountId == null) {
            return "redirect:/login";
        }

        if (hospitalService.getPatientByAccountId(accountId).isPresent()) {
            return "redirect:/patient/dashboard";
        }

        patient.setPatientId(null);
        patient.setAccountId(accountId);

        hospitalService.savePatient(patient);

        return "redirect:/patient/dashboard";
    }

    // Open Patient Book Appointment Page
    @GetMapping("/patient/appointments/book")
    public String showBookAppointmentPage(
            HttpSession session,
            Model model) {

        Long accountId =
                (Long) session.getAttribute("loggedInAdminId");

        if (accountId == null) {
            return "redirect:/login";
        }

        Optional<Patient> patient =
                hospitalService.getPatientByAccountId(accountId);

        if (patient.isEmpty()) {
            return "redirect:/patient/dashboard";
        }

        model.addAttribute("appointment", new Appointment());
        model.addAttribute("doctors", hospitalService.getAllDoctors());

        return "patient-book-appointment";
    }

    // Save Patient Appointment With Validation
    @PostMapping("/patient/appointments/book")
    public String savePatientAppointment(
            @Valid @ModelAttribute("appointment") Appointment appointment,
            BindingResult result,
            HttpSession session,
            Model model) {

        Long accountId =
                (Long) session.getAttribute("loggedInAdminId");

        if (accountId == null) {
            return "redirect:/login";
        }

        Optional<Patient> patient =
                hospitalService.getPatientByAccountId(accountId);

        if (patient.isEmpty()) {
            return "redirect:/patient/dashboard";
        }

        // Validate submitted fields
        if (result.hasErrors()) {
            model.addAttribute("doctors", hospitalService.getAllDoctors());
            model.addAttribute("error", "Please enter a valid appointment date.");
            return "patient-book-appointment";
        }

        // Validate selected doctor
        Optional<Doctor> selectedDoctor =
                hospitalService.getDoctorById(appointment.getDoctorId());

        if (selectedDoctor.isEmpty()) {
            model.addAttribute("doctors", hospitalService.getAllDoctors());
            model.addAttribute("error", "Please select a valid doctor.");
            return "patient-book-appointment";
        }

        Doctor doctor = selectedDoctor.get();

        // Check doctor availability
        if (!"AVAILABLE".equalsIgnoreCase(
                doctor.getAvailabilityStatus())) {

            model.addAttribute("doctors", hospitalService.getAllDoctors());
            model.addAttribute(
                    "error",
                    "This doctor is currently unavailable. Please select another doctor.");

            return "patient-book-appointment";
        }

        // Validate appointment date and time
        LocalDateTime appointmentDateTime;

        try {

            appointmentDateTime =
                    LocalDateTime.parse(appointment.getAppointmentDate());

        } catch (Exception e) {

            model.addAttribute("doctors", hospitalService.getAllDoctors());
            model.addAttribute(
                    "error",
                    "Please enter a valid appointment date and time.");

            return "patient-book-appointment";
        }

        // Prevent past appointments
        if (!appointmentDateTime.isAfter(LocalDateTime.now())) {

            model.addAttribute("doctors", hospitalService.getAllDoctors());
            model.addAttribute(
                    "error",
                    "Appointment date and time must be in the future.");

            return "patient-book-appointment";
        }

        // Prevent duplicate doctor bookings
        if (hospitalService.isDoctorAlreadyBooked(
                doctor.getDoctorId(),
                appointment.getAppointmentDate())) {

            model.addAttribute("doctors", hospitalService.getAllDoctors());
            model.addAttribute(
                    "error",
                    "This doctor already has an appointment at that time. Please choose another time.");

            return "patient-book-appointment";
        }

        // Assign patient ID from logged-in account
        appointment.setAppointmentId(null);
        appointment.setPatientId(patient.get().getPatientId());
        appointment.setStatus("Scheduled");

        hospitalService.saveAppointment(appointment);

        return "redirect:/patient/appointments";
    }

    // View Logged-In Patient Medical Records
    @GetMapping("/patient/medical-records")
    public String showPatientMedicalRecords(
            HttpSession session,
            Model model) {

        Long accountId =
                (Long) session.getAttribute("loggedInAdminId");

        if (accountId == null) {
            return "redirect:/login";
        }

        Optional<Patient> patient =
                hospitalService.getPatientByAccountId(accountId);

        if (patient.isEmpty()) {
            return "redirect:/patient/dashboard";
        }

        Patient loggedInPatient = patient.get();

        model.addAttribute("patient", loggedInPatient);

        List<com.hospital.management.model.MedicalRecord> medicalRecords =
                hospitalService.getMedicalRecordsByPatientId(
                        loggedInPatient.getPatientId());

        for (com.hospital.management.model.MedicalRecord record : medicalRecords) {

            if (record.getDoctorId() != null) {

                hospitalService.getDoctorById(record.getDoctorId())
                        .ifPresent(doctor ->
                                model.addAttribute(
                                        "doctor_" + record.getRecordId(),
                                        doctor.getName()));
            }
        }

        model.addAttribute("medicalRecords", medicalRecords);

        return "patient-medical-records";
    }
}

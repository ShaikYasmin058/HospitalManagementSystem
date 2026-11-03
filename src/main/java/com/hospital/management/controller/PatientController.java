package com.hospital.management.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hospital.management.model.Admin;
import com.hospital.management.model.Patient;
import com.hospital.management.service.HospitalService;

import jakarta.validation.Valid;

@Controller
public class PatientController {

    private final HospitalService hospitalService;

    public PatientController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @GetMapping("/patients")
    public String listPatients(
            @RequestParam(required = false) String search,
            Model model) {

        List<Patient> patients = (search != null && !search.isBlank())
                ? hospitalService.searchPatients(search)
                : hospitalService.getAllPatients();

        model.addAttribute("patients", patients);
        model.addAttribute("search", search);

        return "patients";
    }

    @GetMapping("/patients/new")
    public String showPatientForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "patient-form";
    }

    @PostMapping("/patients")
    public String savePatient(
            @Valid @ModelAttribute("patient") Patient patient,
            BindingResult result,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String password,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "patient-form";
        }

        // Existing patient
        if (patient.getPatientId() != null) {

            Patient existingPatient = hospitalService
                    .getPatientById(patient.getPatientId())
                    .orElseThrow();

            // Existing patient without a login account
            if (existingPatient.getAccountId() == null) {

                if (username == null || username.isBlank()
                        || email == null || email.isBlank()
                        || password == null || password.isBlank()) {

                    model.addAttribute(
                            "error",
                            "Username, email and password are required.");

                    return "patient-form";
                }

                boolean registered = hospitalService
                        .registerLoginForExistingPatient(
                                existingPatient.getPatientId(),
                                username.trim(),
                                email.trim(),
                                password);

                if (!registered) {
                    model.addAttribute(
                            "error",
                            "Username or email already exists.");

                    return "patient-form";
                }

                redirectAttributes.addFlashAttribute(
                        "success",
                        "Patient login account linked successfully.");

                return "redirect:/patients";
            }

            // Existing patient with an account
            patient.setAccountId(existingPatient.getAccountId());
            hospitalService.savePatient(patient);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Patient updated successfully.");

            return "redirect:/patients";
        }

        // New patient
        if (username == null || username.isBlank()
                || email == null || email.isBlank()
                || password == null || password.isBlank()) {

            model.addAttribute(
                    "error",
                    "Username, email and password are required.");

            return "patient-form";
        }

        Admin admin = new Admin();
        admin.setUsername(username.trim());
        admin.setEmail(email.trim());
        admin.setPassword(password);

        boolean registered = hospitalService
                .registerPatientWithAccount(patient, admin);

        if (!registered) {
            model.addAttribute(
                    "error",
                    "Username or email already exists.");

            return "patient-form";
        }

        redirectAttributes.addFlashAttribute(
                "success",
                "Patient and login account created successfully.");

        return "redirect:/patients";
    }

    @GetMapping("/patients/edit/{id}")
    public String editPatient(
            @PathVariable Long id,
            Model model) {

        Patient patient = hospitalService
                .getPatientById(id)
                .orElseThrow();

        model.addAttribute("patient", patient);

        return "patient-form";
    }

    @GetMapping("/patients/delete/{id}")
    public String deletePatient(@PathVariable Long id) {
        hospitalService.deletePatient(id);
        return "redirect:/patients";
    }
}

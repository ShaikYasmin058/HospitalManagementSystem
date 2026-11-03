package com.hospital.management.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hospital.management.model.Admin;
import com.hospital.management.model.Doctor;
import com.hospital.management.service.HospitalService;

import jakarta.validation.Valid;

@Controller
public class DoctorController {

    private final HospitalService hospitalService;

    public DoctorController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    // PUBLIC DOCTOR DIRECTORY
    @GetMapping("/directory/doctors")
    public String publicDoctorDirectory(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String specialization,
            Model model) {

        List<Doctor> doctors = hospitalService.getAllDoctors();

        if (search != null && !search.isBlank()) {
            String keyword = search.trim().toLowerCase();

            doctors = doctors.stream()
                    .filter(doctor ->
                            (doctor.getName() != null
                                    && doctor.getName().toLowerCase()
                                            .contains(keyword))
                            ||
                            (doctor.getSpecialization() != null
                                    && doctor.getSpecialization()
                                            .toLowerCase()
                                            .contains(keyword)))
                    .toList();
        }

        if (specialization != null && !specialization.isBlank()) {
            String selectedSpecialization = specialization.trim();

            doctors = doctors.stream()
                    .filter(doctor ->
                            doctor.getSpecialization() != null
                                    && doctor.getSpecialization()
                                            .equalsIgnoreCase(
                                                    selectedSpecialization))
                    .toList();
        }

        List<String> specializations =
                hospitalService.getAllDoctors()
                        .stream()
                        .map(Doctor::getSpecialization)
                        .filter(value ->
                                value != null && !value.isBlank())
                        .distinct()
                        .sorted(String.CASE_INSENSITIVE_ORDER)
                        .toList();

        model.addAttribute("doctors", doctors);
        model.addAttribute("search", search);
        model.addAttribute("specializations", specializations);
        model.addAttribute("selectedSpecialization", specialization);

        return "doctor-directory";
    }

    // PUBLIC INDIVIDUAL DOCTOR PROFILE
    @GetMapping("/directory/doctors/{id}")
    public String publicDoctorProfile(
            @PathVariable Long id,
            Model model) {

        Doctor doctor = hospitalService.getDoctorById(id)
                .orElseThrow();

        model.addAttribute("doctor", doctor);

        return "doctor-profile";
    }

    // ADMIN DOCTOR MANAGEMENT
    @GetMapping("/doctors")
    public String listDoctors(
            @RequestParam(required = false) String search,
            Model model) {

        List<Doctor> doctors = (search != null && !search.isBlank())
                ? hospitalService.searchDoctors(search)
                : hospitalService.getAllDoctors();

        model.addAttribute("doctors", doctors);
        model.addAttribute("search", search);

        return "doctors";
    }

    @GetMapping("/doctors/new")
    public String showDoctorForm(Model model) {
        model.addAttribute("doctor", new Doctor());
        return "doctor-form";
    }

    @PostMapping("/doctors")
    public String saveDoctor(
            @Valid @ModelAttribute("doctor") Doctor doctor,
            BindingResult result,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) MultipartFile photoFile,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "doctor-form";
        }

        try {

            // Edit existing doctor
            if (doctor.getDoctorId() != null) {

                Doctor existingDoctor = hospitalService
                        .getDoctorById(doctor.getDoctorId())
                        .orElseThrow();

                doctor.setAccountId(existingDoctor.getAccountId());

                if (photoFile != null && !photoFile.isEmpty()) {
                    doctor.setPhoto(saveDoctorPhoto(photoFile));
                } else {
                    doctor.setPhoto(existingDoctor.getPhoto());
                }

                if (doctor.getAvailabilityStatus() == null
                        || doctor.getAvailabilityStatus().isBlank()) {
                    doctor.setAvailabilityStatus(
                            existingDoctor.getAvailabilityStatus());
                }

                hospitalService.saveDoctor(doctor);

                redirectAttributes.addFlashAttribute(
                        "success", "Doctor updated successfully.");

                return "redirect:/doctors";
            }

            // Validate login details
            if (username == null || username.isBlank()
                    || password == null || password.isBlank()
                    || doctor.getEmail() == null
                    || doctor.getEmail().isBlank()) {

                model.addAttribute(
                        "error",
                        "Username, email and password are required.");

                return "doctor-form";
            }

            // Set default availability for new doctor
            if (doctor.getAvailabilityStatus() == null
                    || doctor.getAvailabilityStatus().isBlank()) {
                doctor.setAvailabilityStatus("AVAILABLE");
            }

            // Save doctor photo
            doctor.setPhoto(saveDoctorPhoto(photoFile));

            // Create doctor login
            Admin admin = new Admin();
            admin.setUsername(username.trim());
            admin.setEmail(doctor.getEmail().trim());
            admin.setPassword(password);

            boolean registered = hospitalService
                    .registerDoctorWithAccount(doctor, admin);

            if (!registered) {
                model.addAttribute(
                        "error", "Username or email already exists.");

                return "doctor-form";
            }

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Doctor and login account created successfully.");

            return "redirect:/doctors";

        } catch (IOException e) {

            model.addAttribute(
                    "error", "Photo upload failed: " + e.getMessage());

            return "doctor-form";
        }
    }

    @GetMapping("/doctors/edit/{id}")
    public String editDoctor(
            @PathVariable Long id,
            Model model) {

        Doctor doctor = hospitalService.getDoctorById(id)
                .orElseThrow();

        model.addAttribute("doctor", doctor);

        return "doctor-form";
    }

    @GetMapping("/doctors/delete/{id}")
    public String deleteDoctor(@PathVariable Long id) {
        hospitalService.deleteDoctor(id);
        return "redirect:/doctors";
    }

    // Save Doctor Profile Photo
    private String saveDoctorPhoto(MultipartFile photoFile)
            throws IOException {

        if (photoFile == null || photoFile.isEmpty()) {
            return null;
        }

        String contentType = photoFile.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("Please upload a valid image.");
        }

        String originalName = photoFile.getOriginalFilename();
        String extension = "";

        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(
                    originalName.lastIndexOf(".")).toLowerCase();
        }

        if (!List.of(".jpg", ".jpeg", ".png", ".webp").contains(extension)) {
            throw new IOException(
                    "Only JPG, JPEG, PNG and WEBP images are allowed.");
        }

        Path uploadDirectory = Paths.get("uploads", "doctors");

        Files.createDirectories(uploadDirectory);

        String fileName = UUID.randomUUID() + extension;

        Path filePath = uploadDirectory.resolve(fileName);

        photoFile.transferTo(filePath);

        return "/uploads/doctors/" + fileName;
    }
}
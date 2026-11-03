package com.hospital.management.controller;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hospital.management.model.Doctor;
import com.hospital.management.model.MedicalRecord;
import com.hospital.management.model.Patient;
import com.hospital.management.service.HospitalService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class DoctorMedicalRecordController {

    private final HospitalService hospitalService;

    public DoctorMedicalRecordController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    // View records created by the logged-in doctor
    @GetMapping("/doctor/medical-records")
    public String viewMedicalRecords(
            HttpSession session,
            Model model) {

        Long accountId = (Long) session.getAttribute("loggedInAdminId");

        Doctor doctor = hospitalService
                .getDoctorByAccountId(accountId)
                .orElse(null);

        if (doctor == null) {
            model.addAttribute("error", "Doctor profile not linked.");
            return "doctor-medical-records";
        }

        model.addAttribute("doctor", doctor);
        model.addAttribute(
                "medicalRecords",
                hospitalService.getMedicalRecordsByDoctorId(
                        doctor.getDoctorId()));

        return "doctor-medical-records";
    }

    // Show medical record form for an assigned patient
    @GetMapping("/doctor/medical-records/new")
    public String showRecordForm(
            @RequestParam Long patientId,
            HttpSession session,
            Model model) {

        Long accountId = (Long) session.getAttribute("loggedInAdminId");

        Doctor doctor = hospitalService
                .getDoctorByAccountId(accountId)
                .orElse(null);

        if (doctor == null) {
            return "redirect:/doctor/dashboard";
        }

        Patient patient = hospitalService.getPatientById(patientId)
                .orElse(null);

        if (patient == null
                || !hospitalService.hasPatientAppointmentWithDoctor(
                        patientId, doctor.getDoctorId())) {
            return "redirect:/doctor/appointments";
        }

        MedicalRecord record = new MedicalRecord();
        record.setPatientId(patientId);
        record.setRecordDate(LocalDate.now());

        model.addAttribute("medicalRecord", record);
        model.addAttribute("patient", patient);
        model.addAttribute("doctor", doctor);

        return "doctor-medical-record-form";
    }

    // Save medical record
    @PostMapping("/doctor/medical-records")
    public String saveMedicalRecord(
            @Valid @ModelAttribute("medicalRecord") MedicalRecord record,
            BindingResult result,
            HttpSession session,
            Model model) {

        Long accountId = (Long) session.getAttribute("loggedInAdminId");

        Doctor doctor = hospitalService
                .getDoctorByAccountId(accountId)
                .orElse(null);

        if (doctor == null) {
            return "redirect:/doctor/dashboard";
        }

        Long patientId = record.getPatientId();

        Patient patient = patientId == null
                ? null
                : hospitalService.getPatientById(patientId).orElse(null);

        if (patient == null
                || !hospitalService.hasPatientAppointmentWithDoctor(
                        patientId, doctor.getDoctorId())) {
            return "redirect:/doctor/appointments";
        }

        if (result.hasErrors()) {
            model.addAttribute("patient", patient);
            model.addAttribute("doctor", doctor);
            return "doctor-medical-record-form";
        }

        record.setRecordId(null);
        record.setDoctorId(doctor.getDoctorId());

        if (record.getRecordDate() == null) {
            record.setRecordDate(LocalDate.now());
        }

        hospitalService.saveMedicalRecord(record);

        return "redirect:/doctor/medical-records";
    }
}

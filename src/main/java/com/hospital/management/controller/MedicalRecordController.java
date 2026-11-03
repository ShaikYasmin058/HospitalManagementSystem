package com.hospital.management.controller;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.hospital.management.model.MedicalRecord;
import com.hospital.management.repository.MedicalRecordRepository;
import com.hospital.management.service.HospitalService;

import jakarta.validation.Valid;

@Controller
public class MedicalRecordController {

    private final MedicalRecordRepository medicalRecordRepository;
    private final HospitalService hospitalService;

    public MedicalRecordController(
            MedicalRecordRepository medicalRecordRepository,
            HospitalService hospitalService) {

        this.medicalRecordRepository = medicalRecordRepository;
        this.hospitalService = hospitalService;
    }

    // View all medical records
    @GetMapping("/medical-records")
    public String listMedicalRecords(Model model) {

        model.addAttribute(
                "medicalRecords",
                medicalRecordRepository.findAll());

        model.addAttribute(
                "patients",
                hospitalService.getAllPatients());

        return "medical-records";
    }

    // Show add medical record form
    @GetMapping("/medical-records/new")
    public String showMedicalRecordForm(Model model) {

        MedicalRecord record = new MedicalRecord();
        record.setRecordDate(LocalDate.now());

        model.addAttribute("medicalRecord", record);
        model.addAttribute("patients", hospitalService.getAllPatients());

        return "medical-record-form";
    }

    // Save medical record
    @PostMapping("/medical-records")
    public String saveMedicalRecord(
            @Valid @ModelAttribute("medicalRecord") MedicalRecord record,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("patients", hospitalService.getAllPatients());
            return "medical-record-form";
        }

        medicalRecordRepository.save(record);

        return "redirect:/medical-records";
    }

    // View patient's medical history
    @GetMapping("/medical-records/patient/{patientId}")
    public String patientMedicalHistory(
            @PathVariable Long patientId,
            Model model) {

        model.addAttribute(
                "patient",
                hospitalService.getPatientById(patientId).orElseThrow());

        model.addAttribute(
                "medicalRecords",
                medicalRecordRepository
                        .findByPatientIdOrderByRecordDateDesc(patientId));

        return "patient-medical-history";
    }

    // Edit medical record
    @GetMapping("/medical-records/edit/{id}")
    public String editMedicalRecord(
            @PathVariable Long id,
            Model model) {

        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow();

        model.addAttribute("medicalRecord", record);
        model.addAttribute("patients", hospitalService.getAllPatients());

        return "medical-record-form";
    }

    // Delete medical record
    @GetMapping("/medical-records/delete/{id}")
    public String deleteMedicalRecord(@PathVariable Long id) {

        medicalRecordRepository.deleteById(id);

        return "redirect:/medical-records";
    }
}
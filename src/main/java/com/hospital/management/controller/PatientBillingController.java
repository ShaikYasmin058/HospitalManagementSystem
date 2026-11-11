package com.hospital.management.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.hospital.management.model.Patient;
import com.hospital.management.repository.BillRepository;
import com.hospital.management.service.HospitalService;

import jakarta.servlet.http.HttpSession;

@Controller
public class PatientBillingController {

    private final BillRepository billRepository;
    private final HospitalService hospitalService;

    public PatientBillingController(
            BillRepository billRepository,
            HospitalService hospitalService) {

        this.billRepository = billRepository;
        this.hospitalService = hospitalService;
    }

    @GetMapping("/patient/billing")
    public String showPatientBilling(
            HttpSession session,
            Model model) {

        Long accountId
                = (Long) session.getAttribute("loggedInAdminId");

        if (accountId == null) {
            return "redirect:/login";
        }

        Optional<Patient> patient
                = hospitalService.getPatientByAccountId(accountId);

        if (patient.isEmpty()) {
            return "redirect:/patient/dashboard";
        }

        Patient loggedInPatient = patient.get();

        model.addAttribute("patient", loggedInPatient);

        model.addAttribute(
                "bills",
                billRepository.findByPatientIdOrderByBillDateDesc(
                        loggedInPatient.getPatientId()
                )
        );

        return "patient-billing";
    }
}

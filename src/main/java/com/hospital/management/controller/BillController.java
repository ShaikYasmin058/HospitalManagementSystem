
package com.hospital.management.controller;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.hospital.management.model.Bill;
import com.hospital.management.repository.BillRepository;
import com.hospital.management.service.HospitalService;

import jakarta.validation.Valid;

@Controller
public class BillController {

    private final BillRepository billRepository;
    private final HospitalService hospitalService;

    public BillController(
            BillRepository billRepository,
            HospitalService hospitalService) {

        this.billRepository = billRepository;
        this.hospitalService = hospitalService;
    }

    // View all bills
    @GetMapping("/billing")
    public String listBills(Model model) {

        model.addAttribute("bills", billRepository.findAll());
        model.addAttribute("patients", hospitalService.getAllPatients());

        return "billing";
    }

    // Show new bill form
    @GetMapping("/billing/new")
    public String showBillForm(Model model) {

        Bill bill = new Bill();
        bill.setBillDate(LocalDate.now());
        bill.setPaymentStatus("Pending");

        model.addAttribute("bill", bill);
        model.addAttribute("patients", hospitalService.getAllPatients());

        return "bill-form";
    }

    // Save or update bill
    @PostMapping("/billing")
    public String saveBill(
            @Valid @ModelAttribute("bill") Bill bill,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("patients", hospitalService.getAllPatients());
            return "bill-form";
        }

        double total = bill.getConsultationCharge()
                + bill.getMedicineCharge()
                + bill.getTreatmentCharge();

        bill.setTotalAmount(total);

        billRepository.save(bill);

        return "redirect:/billing";
    }

    // Edit bill
    @GetMapping("/billing/edit/{id}")
    public String editBill(
            @PathVariable Long id,
            Model model) {

        Bill bill = billRepository.findById(id).orElseThrow();

        model.addAttribute("bill", bill);
        model.addAttribute("patients", hospitalService.getAllPatients());

        return "bill-form";
    }

    // Delete bill
    @GetMapping("/billing/delete/{id}")
    public String deleteBill(@PathVariable Long id) {

        billRepository.deleteById(id);

        return "redirect:/billing";
    }
}

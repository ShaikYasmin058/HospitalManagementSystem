
package com.hospital.management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hospital.management.model.Staff;
import com.hospital.management.repository.StaffRepository;

import jakarta.validation.Valid;

@Controller
public class StaffController {

    private final StaffRepository staffRepository;

    public StaffController(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    // View all staff
    @GetMapping("/staff")
    public String listStaff(
            @RequestParam(required = false) String keyword,
            Model model) {

        if (keyword != null && !keyword.trim().isEmpty()) {
            model.addAttribute("staffList",
                    staffRepository
                            .findByNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase(
                                    keyword, keyword));
        } else {
            model.addAttribute("staffList", staffRepository.findAll());
        }

        model.addAttribute("keyword", keyword);

        return "staff";
    }

    // Show add staff form
    @GetMapping("/staff/new")
    public String showStaffForm(Model model) {

        model.addAttribute("staff", new Staff());

        return "staff-form";
    }

    // Save or update staff
    @PostMapping("/staff")
    public String saveStaff(
            @Valid @ModelAttribute("staff") Staff staff,
            BindingResult result) {

        if (result.hasErrors()) {
            return "staff-form";
        }

        staffRepository.save(staff);

        return "redirect:/staff";
    }

    // Edit staff
    @GetMapping("/staff/edit/{id}")
    public String editStaff(
            @PathVariable Long id,
            Model model) {

        Staff staff = staffRepository.findById(id).orElseThrow();

        model.addAttribute("staff", staff);

        return "staff-form";
    }

    // Delete staff
    @GetMapping("/staff/delete/{id}")
    public String deleteStaff(@PathVariable Long id) {

        staffRepository.deleteById(id);

        return "redirect:/staff";
    }
}

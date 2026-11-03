package com.hospital.management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hospital.management.model.Admin;
import com.hospital.management.model.Role;
import com.hospital.management.service.HospitalService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    private final HospitalService hospitalService;

    public AuthController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    // Display login page
    @GetMapping("/login")
    public String showLoginPage(HttpSession session) {

        if (session.getAttribute("loggedInAdmin") != null) {

            String userRole = (String) session.getAttribute("userRole");

            if ("ADMIN".equals(userRole)) {
                return "redirect:/dashboard";

            } else if ("PATIENT".equals(userRole)) {
                return "redirect:/patient/dashboard";

            } else if ("DOCTOR".equals(userRole)) {
                return "redirect:/doctor/dashboard";

            } else if ("RECEPTIONIST".equals(userRole)) {
                return "redirect:/receptionist/dashboard";
            }
        }

        return "login";
    }

    // Process login
    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletRequest request,
            Model model) {

        var adminOptional
                = hospitalService.authenticateAdmin(username, password);

        if (adminOptional.isPresent()) {

            Admin admin = adminOptional.get();

            if (admin.getRole() == null) {
                model.addAttribute("error", "User role is not assigned.");
                return "login";
            }

            HttpSession session = request.getSession(true);
            request.changeSessionId();

            session.setAttribute("loggedInAdmin", admin.getUsername());
            session.setAttribute("userRole", admin.getRole().name());
            session.setAttribute("loggedInAdminId", admin.getId());

            // Redirect according to user role
            Role role = admin.getRole();

            if (role == Role.ADMIN) {
                return "redirect:/dashboard";

            } else if (role == Role.PATIENT) {
                return "redirect:/patient/dashboard";

            } else if (role == Role.DOCTOR) {
                return "redirect:/doctor/dashboard";

            } else if (role == Role.RECEPTIONIST) {
                return "redirect:/receptionist/dashboard";
            }
        }

        model.addAttribute("error", "Invalid username or password");
        return "login";
    }

    // Display registration page
    @GetMapping("/register")
    public String showRegistrationPage() {
        return "register";
    }

    // Process registration
    @PostMapping("/register")
    public String register(
            @RequestParam String fullName,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }

        Admin admin = new Admin();

        // Public registration always creates a PATIENT account
        admin.setRole(Role.PATIENT);
        admin.setFullName(fullName);
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setPassword(password);

        boolean registered = hospitalService.registerAdmin(admin);

        if (!registered) {
            model.addAttribute(
                    "error",
                    "Username or email already exists");
            return "register";
        }

        model.addAttribute(
                "success",
                "Registration successful! Please login.");

        return "login";
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return "redirect:/login";
    }
}

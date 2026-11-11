package com.hospital.management.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        // Check login
        if (session == null
                || session.getAttribute("loggedInAdmin") == null) {

            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        String userRole = (String) session.getAttribute("userRole");
        String path = request.getServletPath();
        System.out.println("========== AUTH DEBUG ==========");
        System.out.println("REQUEST METHOD: " + request.getMethod());
        System.out.println("REQUEST PATH: " + path);
        System.out.println("USER ROLE: " + userRole);
        System.out.println("SESSION USER: " + session.getAttribute("loggedInAdmin"));
        System.out.println("================================");

        if (userRole == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        // ADMIN access
        if ("ADMIN".equals(userRole)) {
            return true;
        }

        // PATIENT access
        if ("PATIENT".equals(userRole)
                && (path.equals("/patient/dashboard")
                || path.equals("/patient/appointments")
                || path.equals("/patient/appointments/book")
                || path.equals("/patient/profile/new")
                || path.equals("/patient/profile")
                || path.equals("/patient/medical-records")
                || path.equals("/patient/billing")
                || path.startsWith("/patient/appointments/cancel/"))) {

            return true;
        }

        // DOCTOR access
        // DOCTOR access
        if ("DOCTOR".equals(userRole)
                && (path.equals("/doctor/dashboard")
                || path.equals("/doctor/appointments")
                || path.equals("/doctor/medical-records")
                || path.startsWith("/doctor/medical-records/"))) {

            return true;
        }

        // RECEPTIONIST access
        if ("RECEPTIONIST".equals(userRole)
                && path.equals("/receptionist/dashboard")) {
            return true;
        }

        // Deny unauthorized access
        response.sendError(
                HttpServletResponse.SC_FORBIDDEN,
                "Access Denied: You do not have permission.");

        return false;
    }
}

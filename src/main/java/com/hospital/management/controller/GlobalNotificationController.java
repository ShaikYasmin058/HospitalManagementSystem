package com.hospital.management.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.hospital.management.model.Appointment;
import com.hospital.management.service.HospitalService;

@ControllerAdvice
public class GlobalNotificationController {

    private final HospitalService hospitalService;

    public GlobalNotificationController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    private List<Appointment> findScheduledAppointments() {
        return hospitalService.getAllAppointments()
                .stream()
                .filter(appointment
                        -> "Scheduled".equalsIgnoreCase(appointment.getStatus()))
                .toList();
    }

    @ModelAttribute("notificationCount")
    public long getNotificationCount() {
        return findScheduledAppointments().size();
    }

    @ModelAttribute("scheduledNotifications")
    public List<Appointment> getScheduledNotifications() {
        return findScheduledAppointments();
    }

    @ModelAttribute("notificationPatientNames")
    public Map<Long, String> getNotificationPatientNames() {

        Map<Long, String> names = new HashMap<>();

        for (Appointment appointment : findScheduledAppointments()) {
            hospitalService.getPatientById(appointment.getPatientId())
                    .ifPresent(patient
                            -> names.put(appointment.getPatientId(), patient.getName()));
        }

        return names;
    }

    @ModelAttribute("notificationDoctorNames")
    public Map<Long, String> getNotificationDoctorNames() {

        Map<Long, String> names = new HashMap<>();

        for (Appointment appointment : findScheduledAppointments()) {
            hospitalService.getDoctorById(appointment.getDoctorId())
                    .ifPresent(doctor
                            -> names.put(appointment.getDoctorId(), doctor.getName()));
        }

        return names;
    }
}

package com.hospital.management.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.management.model.Admin;
import com.hospital.management.model.Appointment;
import com.hospital.management.model.Doctor;
import com.hospital.management.model.MedicalRecord;
import com.hospital.management.model.Patient;
import com.hospital.management.repository.AdminRepository;
import com.hospital.management.repository.AppointmentRepository;
import com.hospital.management.repository.DoctorRepository;
import com.hospital.management.repository.MedicalRecordRepository;
import com.hospital.management.repository.PatientRepository;

@Service
public class HospitalService {

    private final AdminRepository adminRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final PasswordEncoder passwordEncoder;

    public HospitalService(
            AdminRepository adminRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository,
            MedicalRecordRepository medicalRecordRepository,
            PasswordEncoder passwordEncoder) {

        this.adminRepository = adminRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Admin Authentication
    public Optional<Admin> authenticateAdmin(String username, String password) {
        Optional<Admin> adminOptional = adminRepository.findByUsername(username);

        if (adminOptional.isPresent()
                && passwordEncoder.matches(password, adminOptional.get().getPassword())) {
            return adminOptional;
        }

        return Optional.empty();
    }

    // Patient Count
    public long countPatients() {
        return patientRepository.count();
    }

    // Doctor Count
    public long countDoctors() {
        return doctorRepository.count();
    }

    // Appointment Count
    public long countAppointments() {
        return appointmentRepository.count();
    }

    // Get All Patients
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    // Search Patients
    public List<Patient> searchPatients(String term) {
        return patientRepository.findByNameContainingIgnoreCase(term);
    }

    // Save Patient
    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    // Get Patient By ID
    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    // Get Patient By Account ID
    public Optional<Patient> getPatientByAccountId(Long accountId) {
        return patientRepository.findByAccountId(accountId);
    }
    // Get Admin Account By ID

    public Optional<Admin> getAdminById(Long id) {
        return adminRepository.findById(id);
    }

    // Get All Doctors
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    // Search Doctors
    public List<Doctor> searchDoctors(String term) {
        return doctorRepository
                .findByNameContainingIgnoreCaseOrSpecializationContainingIgnoreCase(term, term);
    }

    // Save Doctor
    public Doctor saveDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    // Delete Doctor
    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
    }

    // Get Doctor By ID
    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    // Get All Appointments
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    // Save Appointment
    public Appointment saveAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    // Check Doctor Availability
    public boolean isDoctorAlreadyBooked(Long doctorId, String appointmentDate) {
        return appointmentRepository
                .existsByDoctorIdAndAppointmentDateAndStatusNot(
                        doctorId, appointmentDate, "Cancelled");
    }

    // Delete Patient
    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }

    // Cancel Appointment
    public void cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow();
        appointment.setStatus("Cancelled");
        appointmentRepository.save(appointment);
    }

    // Cancel Appointment By Patient
    public boolean cancelPatientAppointment(Long appointmentId, Long patientId) {

        Optional<Appointment> appointmentOptional
                = appointmentRepository.findByAppointmentIdAndPatientId(
                        appointmentId, patientId);

        if (appointmentOptional.isEmpty()) {
            return false;
        }

        Appointment appointment = appointmentOptional.get();

        if ("Completed".equalsIgnoreCase(appointment.getStatus())
                || "Cancelled".equalsIgnoreCase(appointment.getStatus())) {
            return false;
        }

        appointment.setStatus("Cancelled");
        appointmentRepository.save(appointment);

        return true;
    }

    // Get Appointment By ID
    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    // Register Admin
    public boolean registerAdmin(Admin admin) {

        if (adminRepository.findByUsername(admin.getUsername()).isPresent()) {
            return false;
        }

        if (adminRepository.findByEmail(admin.getEmail()).isPresent()) {
            return false;
        }

        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        adminRepository.save(admin);

        return true;
    }

    // Register Patient With Account
    @Transactional
    public boolean registerPatientWithAccount(Patient patient, Admin admin) {

        if (adminRepository.findByUsername(admin.getUsername()).isPresent()) {
            return false;
        }

        if (adminRepository.findByEmail(admin.getEmail()).isPresent()) {
            return false;
        }

        admin.setRole(com.hospital.management.model.Role.PATIENT);
        admin.setFullName(patient.getName());
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));

        Admin savedAdmin = adminRepository.save(admin);

        patient.setAccountId(savedAdmin.getId());
        patientRepository.save(patient);

        return true;
    }
    // Create Login Account For Existing Patient

    @Transactional
    public boolean registerLoginForExistingPatient(
            Long patientId,
            String username,
            String email,
            String password) {

        Patient patient = patientRepository.findById(patientId)
                .orElse(null);

        if (patient == null || patient.getAccountId() != null) {
            return false;
        }

        if (adminRepository.findByUsername(username).isPresent()
                || adminRepository.findByEmail(email).isPresent()) {
            return false;
        }

        Admin admin = new Admin();
        admin.setUsername(username.trim());
        admin.setEmail(email.trim());
        admin.setFullName(patient.getName());
        admin.setRole(com.hospital.management.model.Role.PATIENT);
        admin.setPassword(passwordEncoder.encode(password));

        Admin savedAdmin = adminRepository.save(admin);

        patient.setAccountId(savedAdmin.getId());
        patientRepository.save(patient);

        return true;
    }

    // Get Medical Records By Doctor ID
    public List<MedicalRecord> getMedicalRecordsByDoctorId(Long doctorId) {
        return medicalRecordRepository
                .findByDoctorIdOrderByRecordDateDesc(doctorId);
    }

    // Save Medical Record
    public MedicalRecord saveMedicalRecord(MedicalRecord record) {
        return medicalRecordRepository.save(record);
    }

    // Check whether a patient has an appointment with a doctor
    public boolean hasPatientAppointmentWithDoctor(
            Long patientId, Long doctorId) {

        return appointmentRepository.findByDoctorId(doctorId)
                .stream()
                .anyMatch(appointment
                        -> patientId.equals(appointment.getPatientId())
                        && !"Cancelled".equalsIgnoreCase(
                                appointment.getStatus()));
    }

    // Register Doctor With Login Account
    @Transactional
    public boolean registerDoctorWithAccount(Doctor doctor, Admin admin) {

        String username = admin.getUsername().trim();
        String email = admin.getEmail().trim();

        // Check duplicate username
        if (adminRepository.findByUsername(username).isPresent()) {
            System.out.println("DUPLICATE USERNAME: " + username);
            return false;
        }

        // Check duplicate email
        if (adminRepository.findByEmail(email).isPresent()) {
            System.out.println("DUPLICATE EMAIL: " + email);
            return false;
        }

        admin.setUsername(username);
        admin.setEmail(email);
        admin.setRole(com.hospital.management.model.Role.DOCTOR);
        admin.setFullName(doctor.getName());
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));

        Admin savedAdmin = adminRepository.save(admin);

        doctor.setAccountId(savedAdmin.getId());
        doctorRepository.save(doctor);

        return true;
    }

    // Get Appointments By Patient ID
    public List<Appointment> getAppointmentsByPatientId(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    // Get Appointments By Doctor ID
    public List<Appointment> getAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    // Get Doctor By Account ID
    public Optional<Doctor> getDoctorByAccountId(Long accountId) {
        return doctorRepository.findByAccountId(accountId);
    }

    // Get Medical Records By Patient ID
    public List<MedicalRecord> getMedicalRecordsByPatientId(Long patientId) {
        return medicalRecordRepository
                .findByPatientIdOrderByRecordDateDesc(patientId);
    }
}

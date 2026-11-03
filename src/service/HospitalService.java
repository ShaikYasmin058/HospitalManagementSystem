package service;

import java.util.List;
import java.util.Scanner;
import dao.AdminDAO;
import dao.AppointmentDAO;
import dao.DoctorDAO;
import dao.PatientDAO;
import model.Appointment;
import model.Doctor;
import model.Patient;
import util.ValidationUtil;

public class HospitalService {

    private final AdminDAO adminDAO = new AdminDAO();
    private final PatientDAO patientDAO = new PatientDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final Scanner scanner = new Scanner(System.in);

    public boolean login() {
        System.out.println("\n=== Admin Login ===");
        String username = ValidationUtil.readRequiredString(scanner, "Enter username: ");
        String password = ValidationUtil.readRequiredString(scanner, "Enter password: ");
        boolean valid = adminDAO.authenticate(username, password);
        if (valid) {
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid username or password.");
        }
        return valid;
    }

    public void addPatient() {
        System.out.println("\n=== Add Patient ===");
        String name = ValidationUtil.readRequiredString(scanner, "Enter patient name: ");
        if (!ValidationUtil.isValidName(name)) {
            System.out.println("Invalid name. Use letters and spaces only.");
            return;
        }
        int age = ValidationUtil.readInt(scanner, "Enter age: ");
        if (!ValidationUtil.isValidAge(age)) {
            System.out.println("Age must be between 1 and 150.");
            return;
        }
        String gender = ValidationUtil.readRequiredString(scanner, "Enter gender: ");
        String phone = ValidationUtil.readRequiredString(scanner, "Enter phone number: ");
        if (!ValidationUtil.isValidPhone(phone)) {
            System.out.println("Phone number must be exactly 10 digits.");
            return;
        }
        String disease = ValidationUtil.readRequiredString(scanner, "Enter disease: ");

        Patient patient = new Patient();
        patient.setName(name);
        patient.setAge(age);
        patient.setGender(gender);
        patient.setPhone(phone);
        patient.setDisease(disease);

        if (patientDAO.addPatient(patient)) {
            System.out.println("Patient added successfully.");
        } else {
            System.out.println("Could not add patient.");
        }
    }

    public void viewAllPatients() {
        List<Patient> patients = patientDAO.getAllPatients();
        if (patients.isEmpty()) {
            System.out.println("No patients found.");
            return;
        }
        for (Patient patient : patients) {
            System.out.println(patient);
        }
    }

    public void searchPatient() {
        int id = ValidationUtil.readInt(scanner, "Enter patient ID: ");
        Patient patient = patientDAO.getPatientById(id);
        if (patient != null) {
            System.out.println(patient);
        } else {
            System.out.println("Patient not found.");
        }
    }

    public void updatePatient() {
        int id = ValidationUtil.readInt(scanner, "Enter patient ID to update: ");
        Patient existing = patientDAO.getPatientById(id);
        if (existing == null) {
            System.out.println("Patient not found.");
            return;
        }
        System.out.println("Current patient: " + existing);
        String name = ValidationUtil.readRequiredString(scanner, "Enter new name: ");
        int age = ValidationUtil.readInt(scanner, "Enter new age: ");
        String gender = ValidationUtil.readRequiredString(scanner, "Enter new gender: ");
        String phone = ValidationUtil.readRequiredString(scanner, "Enter new phone: ");
        String disease = ValidationUtil.readRequiredString(scanner, "Enter new disease: ");

        existing.setName(name);
        existing.setAge(age);
        existing.setGender(gender);
        existing.setPhone(phone);
        existing.setDisease(disease);

        if (patientDAO.updatePatient(existing)) {
            System.out.println("Patient updated successfully.");
        } else {
            System.out.println("Patient update failed.");
        }
    }

    public void deletePatient() {
        int id = ValidationUtil.readInt(scanner, "Enter patient ID to delete: ");
        if (patientDAO.deletePatient(id)) {
            System.out.println("Patient deleted successfully.");
        } else {
            System.out.println("Patient deletion failed.");
        }
    }

    public void addDoctor() {
        System.out.println("\n=== Add Doctor ===");
        String name = ValidationUtil.readRequiredString(scanner, "Enter doctor name: ");
        if (!ValidationUtil.isValidName(name)) {
            System.out.println("Invalid name. Use letters and spaces only.");
            return;
        }
        String specialization = ValidationUtil.readRequiredString(scanner, "Enter specialization: ");
        String phone = ValidationUtil.readRequiredString(scanner, "Enter phone number: ");
        if (!ValidationUtil.isValidPhone(phone)) {
            System.out.println("Phone number must be exactly 10 digits.");
            return;
        }
        String email = ValidationUtil.readRequiredString(scanner, "Enter email: ");
        if (!ValidationUtil.isValidEmail(email)) {
            System.out.println("Invalid email format.");
            return;
        }

        Doctor doctor = new Doctor();
        doctor.setName(name);
        doctor.setSpecialization(specialization);
        doctor.setPhone(phone);
        doctor.setEmail(email);

        if (doctorDAO.addDoctor(doctor)) {
            System.out.println("Doctor added successfully.");
        } else {
            System.out.println("Could not add doctor.");
        }
    }

    public void viewAllDoctors() {
        List<Doctor> doctors = doctorDAO.getAllDoctors();
        if (doctors.isEmpty()) {
            System.out.println("No doctors found.");
            return;
        }
        for (Doctor doctor : doctors) {
            System.out.println(doctor);
        }
    }

    public void searchDoctor() {
        int id = ValidationUtil.readInt(scanner, "Enter doctor ID: ");
        Doctor doctor = doctorDAO.getDoctorById(id);
        if (doctor != null) {
            System.out.println(doctor);
        } else {
            System.out.println("Doctor not found.");
        }
    }

    public void updateDoctor() {
        int id = ValidationUtil.readInt(scanner, "Enter doctor ID to update: ");
        Doctor existing = doctorDAO.getDoctorById(id);
        if (existing == null) {
            System.out.println("Doctor not found.");
            return;
        }
        System.out.println("Current doctor: " + existing);
        String name = ValidationUtil.readRequiredString(scanner, "Enter new name: ");
        String specialization = ValidationUtil.readRequiredString(scanner, "Enter new specialization: ");
        String phone = ValidationUtil.readRequiredString(scanner, "Enter new phone: ");
        String email = ValidationUtil.readRequiredString(scanner, "Enter new email: ");

        existing.setName(name);
        existing.setSpecialization(specialization);
        existing.setPhone(phone);
        existing.setEmail(email);

        if (doctorDAO.updateDoctor(existing)) {
            System.out.println("Doctor updated successfully.");
        } else {
            System.out.println("Doctor update failed.");
        }
    }

    public void deleteDoctor() {
        int id = ValidationUtil.readInt(scanner, "Enter doctor ID to delete: ");
        if (doctorDAO.deleteDoctor(id)) {
            System.out.println("Doctor deleted successfully.");
        } else {
            System.out.println("Doctor deletion failed.");
        }
    }

    public void bookAppointment() {
        System.out.println("\n=== Book Appointment ===");
        int patientId = ValidationUtil.readInt(scanner, "Enter patient ID: ");
        int doctorId = ValidationUtil.readInt(scanner, "Enter doctor ID: ");
        String date = ValidationUtil.readRequiredString(scanner, "Enter appointment date (YYYY-MM-DD): ");
        Appointment appointment = new Appointment();
        appointment.setPatientId(patientId);
        appointment.setDoctorId(doctorId);
        appointment.setAppointmentDate(date);
        appointment.setStatus("Scheduled");
        if (appointmentDAO.bookAppointment(appointment)) {
            System.out.println("Appointment booked successfully.");
        } else {
            System.out.println("Appointment booking failed.");
        }
    }

    public void viewAppointments() {
        List<Appointment> appointments = appointmentDAO.getAllAppointments();
        if (appointments.isEmpty()) {
            System.out.println("No appointments found.");
            return;
        }
        for (Appointment appointment : appointments) {
            System.out.println(appointment);
        }
    }

    public void cancelAppointment() {
        int id = ValidationUtil.readInt(scanner, "Enter appointment ID to cancel: ");
        if (appointmentDAO.cancelAppointment(id)) {
            System.out.println("Appointment cancelled successfully.");
        } else {
            System.out.println("Appointment cancellation failed.");
        }
    }
}

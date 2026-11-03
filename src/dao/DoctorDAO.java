package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import database.DBConnection;
import model.Doctor;

public class DoctorDAO {

    public boolean addDoctor(Doctor doctor) {
        String sql = "INSERT INTO doctor (name, specialization, phone, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, doctor.getName());
            ps.setString(2, doctor.getSpecialization());
            ps.setString(3, doctor.getPhone());
            ps.setString(4, doctor.getEmail());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to add doctor: " + e.getMessage());
            return false;
        }
    }

    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctor";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Doctor doctor = new Doctor();
                doctor.setDoctorId(rs.getInt("doctor_id"));
                doctor.setName(rs.getString("name"));
                doctor.setSpecialization(rs.getString("specialization"));
                doctor.setPhone(rs.getString("phone"));
                doctor.setEmail(rs.getString("email"));
                doctors.add(doctor);
            }
        } catch (SQLException e) {
            System.out.println("Failed to load doctors: " + e.getMessage());
        }
        return doctors;
    }

    public Doctor getDoctorById(int id) {
        String sql = "SELECT * FROM doctor WHERE doctor_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Doctor doctor = new Doctor();
                    doctor.setDoctorId(rs.getInt("doctor_id"));
                    doctor.setName(rs.getString("name"));
                    doctor.setSpecialization(rs.getString("specialization"));
                    doctor.setPhone(rs.getString("phone"));
                    doctor.setEmail(rs.getString("email"));
                    return doctor;
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to search doctor: " + e.getMessage());
        }
        return null;
    }

    public boolean updateDoctor(Doctor doctor) {
        String sql = "UPDATE doctor SET name = ?, specialization = ?, phone = ?, email = ? WHERE doctor_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, doctor.getName());
            ps.setString(2, doctor.getSpecialization());
            ps.setString(3, doctor.getPhone());
            ps.setString(4, doctor.getEmail());
            ps.setInt(5, doctor.getDoctorId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to update doctor: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteDoctor(int id) {
        String sql = "DELETE FROM doctor WHERE doctor_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to delete doctor: " + e.getMessage());
            return false;
        }
    }
}

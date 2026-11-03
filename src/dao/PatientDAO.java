package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import database.DBConnection;
import model.Patient;

public class PatientDAO {

    public boolean addPatient(Patient patient) {
        String sql = "INSERT INTO patient (name, age, gender, phone, disease) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patient.getName());
            ps.setInt(2, patient.getAge());
            ps.setString(3, patient.getGender());
            ps.setString(4, patient.getPhone());
            ps.setString(5, patient.getDisease());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to add patient: " + e.getMessage());
            return false;
        }
    }

    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patient";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Patient patient = new Patient();
                patient.setPatientId(rs.getInt("patient_id"));
                patient.setName(rs.getString("name"));
                patient.setAge(rs.getInt("age"));
                patient.setGender(rs.getString("gender"));
                patient.setPhone(rs.getString("phone"));
                patient.setDisease(rs.getString("disease"));
                patients.add(patient);
            }
        } catch (SQLException e) {
            System.out.println("Failed to load patients: " + e.getMessage());
        }
        return patients;
    }

    public Patient getPatientById(int id) {
        String sql = "SELECT * FROM patient WHERE patient_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Patient patient = new Patient();
                    patient.setPatientId(rs.getInt("patient_id"));
                    patient.setName(rs.getString("name"));
                    patient.setAge(rs.getInt("age"));
                    patient.setGender(rs.getString("gender"));
                    patient.setPhone(rs.getString("phone"));
                    patient.setDisease(rs.getString("disease"));
                    return patient;
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to search patient: " + e.getMessage());
        }
        return null;
    }

    public boolean updatePatient(Patient patient) {
        String sql = "UPDATE patient SET name = ?, age = ?, gender = ?, phone = ?, disease = ? WHERE patient_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patient.getName());
            ps.setInt(2, patient.getAge());
            ps.setString(3, patient.getGender());
            ps.setString(4, patient.getPhone());
            ps.setString(5, patient.getDisease());
            ps.setInt(6, patient.getPatientId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to update patient: " + e.getMessage());
            return false;
        }
    }

    public boolean deletePatient(int id) {
        String sql = "DELETE FROM patient WHERE patient_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to delete patient: " + e.getMessage());
            return false;
        }
    }
}

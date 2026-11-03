package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import database.DBConnection;
import model.Appointment;

public class AppointmentDAO {

    public boolean bookAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointment (patient_id, doctor_id, appointment_date, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, appointment.getPatientId());
            ps.setInt(2, appointment.getDoctorId());
            ps.setString(3, appointment.getAppointmentDate());
            ps.setString(4, appointment.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to book appointment: " + e.getMessage());
            return false;
        }
    }

    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointment";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setAppointmentId(rs.getInt("appointment_id"));
                appointment.setPatientId(rs.getInt("patient_id"));
                appointment.setDoctorId(rs.getInt("doctor_id"));
                appointment.setAppointmentDate(rs.getString("appointment_date"));
                appointment.setStatus(rs.getString("status"));
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            System.out.println("Failed to load appointments: " + e.getMessage());
        }
        return appointments;
    }

    public boolean cancelAppointment(int id) {
        String sql = "UPDATE appointment SET status = 'Cancelled' WHERE appointment_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to cancel appointment: " + e.getMessage());
            return false;
        }
    }
}

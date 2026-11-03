package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Admin;
import database.DBConnection;

public class AdminDAO {

    public boolean authenticate(String username, String password) {
        String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Database error during login: " + e.getMessage());
            return false;
        }
    }

    public boolean addAdmin(Admin admin) {
        String sql = "INSERT INTO admin (username, password) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, admin.getUsername());
            ps.setString(2, admin.getPassword());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to add admin: " + e.getMessage());
            return false;
        }
    }
}

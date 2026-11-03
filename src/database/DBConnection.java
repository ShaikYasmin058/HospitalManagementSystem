package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {

    private static final String URL = System.getProperty(
            "DB_URL",
            System.getenv().getOrDefault(
                    "DB_URL",
                    "jdbc:mysql://localhost:3306/hospital_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
            )
    );
    private static final String USERNAME = System.getProperty("DB_USERNAME", System.getenv().getOrDefault("DB_USERNAME", "root"));
    private static final String PASSWORD = System.getProperty("DB_PASSWORD", System.getenv().getOrDefault("DB_PASSWORD", ""));
    private static final String SERVER_URL = "jdbc:mysql://localhost:3306?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public static void initializeDatabase() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }

        try (Connection conn = DriverManager.getConnection(SERVER_URL, USERNAME, PASSWORD); Statement statement = conn.createStatement()) {
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS hospital_db");
        }

        try (Connection conn = getConnection(); Statement statement = conn.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS admin (admin_id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(50) NOT NULL UNIQUE, password VARCHAR(100) NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS patient (patient_id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL, age INT NOT NULL, gender VARCHAR(20) NOT NULL, phone VARCHAR(15) NOT NULL, disease VARCHAR(100) NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS doctor (doctor_id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL, specialization VARCHAR(100) NOT NULL, phone VARCHAR(15) NOT NULL, email VARCHAR(100) NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS appointment (appointment_id INT AUTO_INCREMENT PRIMARY KEY, patient_id INT NOT NULL, doctor_id INT NOT NULL, appointment_date VARCHAR(20) NOT NULL, status VARCHAR(20) NOT NULL, FOREIGN KEY (patient_id) REFERENCES patient(patient_id) ON DELETE CASCADE, FOREIGN KEY (doctor_id) REFERENCES doctor(doctor_id) ON DELETE CASCADE)");
            statement.executeUpdate("INSERT IGNORE INTO admin (username, password) VALUES ('admin', 'admin123')");
            statement.executeUpdate("INSERT IGNORE INTO patient (name, age, gender, phone, disease) VALUES ('Alice Brown', 30, 'Female', '9876543210', 'Flu')");
            statement.executeUpdate("INSERT IGNORE INTO doctor (name, specialization, phone, email) VALUES ('Dr. Smith', 'Cardiology', '9876501234', 'smith@example.com')");
            statement.executeUpdate("INSERT IGNORE INTO appointment (patient_id, doctor_id, appointment_date, status) VALUES (1, 1, '2026-08-10', 'Scheduled')");
        }
    }
}

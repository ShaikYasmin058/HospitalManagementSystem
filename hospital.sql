CREATE DATABASE IF NOT EXISTS hospital_db;
USE hospital_db;

CREATE TABLE IF NOT EXISTS admin (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS patient (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    gender VARCHAR(20) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    disease VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS doctor (
    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    email VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS appointment (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_date VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (patient_id) REFERENCES patient(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctor(doctor_id) ON DELETE CASCADE
);

INSERT INTO admin (username, password) VALUES ('admin', 'admin123');
INSERT INTO patient (name, age, gender, phone, disease) VALUES ('Alice Brown', 30, 'Female', '9876543210', 'Flu');
INSERT INTO doctor (name, specialization, phone, email) VALUES ('Dr. Smith', 'Cardiology', '9876501234', 'smith@example.com');
INSERT INTO appointment (patient_id, doctor_id, appointment_date, status) VALUES (1, 1, '2026-08-10', 'Scheduled');

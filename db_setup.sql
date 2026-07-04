-- Smart Clinic Management System Database Schema & Seed Data

CREATE DATABASE IF NOT EXISTS clinic_db;
USE clinic_db;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Doctors Table
CREATE TABLE IF NOT EXISTS doctors (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    availability TEXT,
    FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. Patients Table
CREATE TABLE IF NOT EXISTS patients (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    phone VARCHAR(20) NOT NULL,
    medical_history_summary TEXT,
    FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);

-- 4. Appointments Table
CREATE TABLE IF NOT EXISTS appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    appointment_date DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    reason VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);

-- Seed Data (Password hashes are encrypted using BCrypt for password: "password")
-- BCrypt hash of "password": $2a$10$U50DqK9fT/LwF4T2K.jHxu0k1bEaFpP.Fm1z4Fw18L05Y9k9n9t9y (or general $2a$10$8.K1tWvKk/7m/Jc1L6K8U.pE742n4jQ9W1bC8w5H5j7g6e4d.C1p2)
-- We will insert an admin, two doctors, and two patients.

INSERT IGNORE INTO users (id, username, password, email, role) VALUES
(1, 'admin', '$2a$10$Ep.XjLz2/yC1lK8dFzZ8mOEqNbe5w2y5B1tT84W0hR6v8d0y2U1lq', 'admin@smartcare.com', 'ADMIN'),
(2, 'dr_smith', '$2a$10$Ep.XjLz2/yC1lK8dFzZ8mOEqNbe5w2y5B1tT84W0hR6v8d0y2U1lq', 'smith@smartcare.com', 'DOCTOR'),
(3, 'dr_jones', '$2a$10$Ep.XjLz2/yC1lK8dFzZ8mOEqNbe5w2y5B1tT84W0hR6v8d0y2U1lq', 'jones@smartcare.com', 'DOCTOR'),
(4, 'john_doe', '$2a$10$Ep.XjLz2/yC1lK8dFzZ8mOEqNbe5w2y5B1tT84W0hR6v8d0y2U1lq', 'john@gmail.com', 'PATIENT'),
(5, 'jane_doe', '$2a$10$Ep.XjLz2/yC1lK8dFzZ8mOEqNbe5w2y5B1tT84W0hR6v8d0y2U1lq', 'jane@gmail.com', 'PATIENT');

INSERT IGNORE INTO doctors (id, name, specialization, phone, availability) VALUES
(2, 'Dr. Sarah Smith', 'Cardiology', '555-0192', 'Mon, Wed, Fri 09:00 - 14:00'),
(3, 'Dr. Robert Jones', 'Pediatrics', '555-0143', 'Tue, Thu 10:00 - 16:00');

INSERT IGNORE INTO patients (id, name, date_of_birth, phone, medical_history_summary) VALUES
(4, 'John Doe', '1988-11-23', '555-9876', 'Mild asthma, seasonal allergies.'),
(5, 'Jane Doe', '1992-04-15', '555-4321', 'No major conditions.');

INSERT IGNORE INTO appointments (id, patient_id, doctor_id, appointment_date, status, reason) VALUES
(1, 4, 2, '2026-07-06 10:00:00', 'SCHEDULED', 'Routine cardiac health review'),
(2, 5, 3, '2026-07-07 11:30:00', 'SCHEDULED', 'Childhood checkup consultation');


-- Stored Procedure to Get Doctor Appointment Count Statistics
DELIMITER //

DROP PROCEDURE IF EXISTS GetDoctorAppointmentStats //

CREATE PROCEDURE GetDoctorAppointmentStats()
BEGIN
    SELECT d.id AS doctor_id, d.name AS doctor_name, d.specialization, COUNT(a.id) AS appointment_count
    FROM doctors d
    LEFT JOIN appointments a ON d.id = a.doctor_id
    GROUP BY d.id, d.name, d.specialization;
END //

DELIMITER ;

package com.smartcare.clinicsys.service;

import com.smartcare.clinicsys.model.Doctor;
import com.smartcare.clinicsys.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    @Transactional
    public Optional<Doctor> updateDoctor(Long id, Doctor doctorDetails) {
        return doctorRepository.findById(id)
                .map(doctor -> {
                    doctor.setName(doctorDetails.getName());
                    doctor.setSpecialization(doctorDetails.getSpecialization());
                    doctor.setPhone(doctorDetails.getPhone());
                    doctor.setAvailability(doctorDetails.getAvailability());
                    return doctorRepository.save(doctor);
                });
    }

    public List<?> getDoctorStats() {
        return jdbcTemplate.queryForList("CALL GetDoctorAppointmentStats()");
    }

    /**
     * Retrieves a doctor's available time slots for a specific date.
     * Queries the availability table/records associated with the doctor
     * and filters by the given date.
     */
    public List<String> getAvailableSlots(Long doctorId, LocalDate date) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            return Collections.emptyList();
        }

        String sql = "SELECT time_slot FROM doctor_availability " +
                     "WHERE doctor_id = ? AND available_date = ? AND is_booked = false " +
                     "ORDER BY time_slot ASC";

        return jdbcTemplate.queryForList(sql, String.class, doctorId, date);
    }

    /**
     * Validates a doctor's login credentials against stored records.
     * NOTE: Replace with proper password hashing (e.g., BCrypt) in production —
     * this assumes a passwordHash field/column, not plain text comparison.
     */
    public boolean validateLogin(String email, String rawPassword) {
        Optional<Doctor> doctorOpt = doctorRepository.findByEmail(email);
        if (doctorOpt.isEmpty()) {
            return false;
        }

        Doctor doctor = doctorOpt.get();
        // TODO: use a PasswordEncoder (e.g., BCryptPasswordEncoder) instead of direct comparison
        return doctor.getPassword() != null && doctor.getPassword().equals(rawPassword);
    }
}

package com.smartcare.clinicsys.service;

import com.smartcare.clinicsys.model.Doctor;
import com.smartcare.clinicsys.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}

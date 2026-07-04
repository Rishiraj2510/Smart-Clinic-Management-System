package com.smartcare.clinicsys.controller;

import com.smartcare.clinicsys.model.Doctor;
import com.smartcare.clinicsys.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public List<?> getDoctorStats() {
        return jdbcTemplate.queryForList("CALL GetDoctorAppointmentStats()");
    }

    @GetMapping
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable Long id) {
        return doctorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<?> updateDoctor(@PathVariable Long id, @RequestBody Doctor doctorDetails) {
        return doctorRepository.findById(id)
                .map(doctor -> {
                    doctor.setName(doctorDetails.getName());
                    doctor.setSpecialization(doctorDetails.getSpecialization());
                    doctor.setPhone(doctorDetails.getPhone());
                    doctor.setAvailability(doctorDetails.getAvailability());
                    Doctor updatedDoctor = doctorRepository.save(doctor);
                    return ResponseEntity.ok(updatedDoctor);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

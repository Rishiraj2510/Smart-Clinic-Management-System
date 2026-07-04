package com.smartcare.clinicsys.controller;

import com.smartcare.clinicsys.model.Prescription;
import com.smartcare.clinicsys.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> createPrescription(@RequestBody Prescription prescription) {
        prescription.setPrescriptionDate(LocalDateTime.now());
        Prescription saved = prescriptionRepository.save(prescription);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('PATIENT')")
    public List<Prescription> getPrescriptionsByPatient(@PathVariable Long patientId) {
        return prescriptionRepository.findByPatientId(patientId);
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public List<Prescription> getPrescriptionsByDoctor(@PathVariable Long doctorId) {
        return prescriptionRepository.findByDoctorId(doctorId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('PATIENT')")
    public ResponseEntity<Prescription> getPrescriptionById(@PathVariable String id) {
        return prescriptionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

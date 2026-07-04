package com.smartcare.clinicsys.controller;

import com.smartcare.clinicsys.model.Patient;
import com.smartcare.clinicsys.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientRepository patientRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('PATIENT')")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        return patientRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PATIENT')")
    public ResponseEntity<?> updatePatient(@PathVariable Long id, @RequestBody Patient patientDetails) {
        return patientRepository.findById(id)
                .map(patient -> {
                    patient.setName(patientDetails.getName());
                    patient.setDateOfBirth(patientDetails.getDateOfBirth());
                    patient.setPhone(patientDetails.getPhone());
                    patient.setMedicalHistorySummary(patientDetails.getMedicalHistorySummary());
                    Patient updatedPatient = patientRepository.save(patient);
                    return ResponseEntity.ok(updatedPatient);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

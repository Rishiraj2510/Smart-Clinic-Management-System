package com.smartcare.clinicsys.controller;

import com.smartcare.clinicsys.dto.AppointmentRequest;
import com.smartcare.clinicsys.dto.MessageResponse;
import com.smartcare.clinicsys.model.Appointment;
import com.smartcare.clinicsys.model.Doctor;
import com.smartcare.clinicsys.model.Patient;
import com.smartcare.clinicsys.repository.AppointmentRepository;
import com.smartcare.clinicsys.repository.DoctorRepository;
import com.smartcare.clinicsys.repository.PatientRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PATIENT')")
    public List<Appointment> getAppointmentsByPatient(@PathVariable Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public List<Appointment> getAppointmentsByDoctor(@PathVariable Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PATIENT')")
    public ResponseEntity<?> createAppointment(@Valid @RequestBody AppointmentRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElse(null);
        if (patient == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Patient not found!"));
        }

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElse(null);
        if (doctor == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Doctor not found!"));
        }

        LocalDateTime date = LocalDateTime.parse(request.getAppointmentDate());

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentDate(date)
                .status("SCHEDULED")
                .reason(request.getReason())
                .build();

        Appointment saved = appointmentRepository.save(appointment);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('PATIENT')")
    public ResponseEntity<?> updateAppointmentStatus(@PathVariable Long id, @RequestParam String status) {
        return appointmentRepository.findById(id)
                .map(appointment -> {
                    appointment.setStatus(status.toUpperCase());
                    Appointment updated = appointmentRepository.save(appointment);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

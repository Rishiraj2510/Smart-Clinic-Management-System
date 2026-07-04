package com.smartcare.clinicsys.service;

import com.smartcare.clinicsys.dto.AppointmentRequest;
import com.smartcare.clinicsys.model.Appointment;
import com.smartcare.clinicsys.model.Doctor;
import com.smartcare.clinicsys.model.Patient;
import com.smartcare.clinicsys.repository.AppointmentRepository;
import com.smartcare.clinicsys.repository.DoctorRepository;
import com.smartcare.clinicsys.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    public List<Appointment> getAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    @Transactional
    public Appointment createAppointment(AppointmentRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        LocalDateTime date = LocalDateTime.parse(request.getAppointmentDate());

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentDate(date)
                .status("SCHEDULED")
                .reason(request.getReason())
                .build();

        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Optional<Appointment> updateAppointmentStatus(Long id, String status) {
        return appointmentRepository.findById(id)
                .map(appointment -> {
                    appointment.setStatus(status.toUpperCase());
                    return appointmentRepository.save(appointment);
                });
    }
}

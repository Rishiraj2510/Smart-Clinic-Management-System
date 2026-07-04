package com.smartcare.clinicsys.repository;

import com.smartcare.clinicsys.model.Prescription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {
    List<Prescription> findByPatientId(Long patientId);
    List<Prescription> findByDoctorId(Long doctorId);
    List<Prescription> findByAppointmentId(Long appointmentId);
}

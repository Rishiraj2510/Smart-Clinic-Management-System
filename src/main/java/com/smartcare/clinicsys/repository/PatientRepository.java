package com.smartcare.clinicsys.repository;

import com.smartcare.clinicsys.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByEmail(String email);

    Optional<Patient> findByPhone(String phone);

    List<Patient> findByEmailOrPhone(String email, String phone);
}

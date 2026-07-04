package com.smartcare.clinicsys.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SignupRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    @Email
    private String email;

    @NotNull
    private String role;

    // Patient specific fields
    private String patientName;
    private String dateOfBirth;
    private String patientPhone;
    private String medicalHistorySummary;

    // Doctor specific fields
    private String doctorName;
    private String specialization;
    private String doctorPhone;
    private String availability;
}

package com.smartcare.clinicsys.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "user")
public class Patient {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Date of Birth is required")
    @Column(name = "date_of_birth", nullable = false)
    private String dateOfBirth;

    @NotBlank(message = "Phone number is required")
    @Column(nullable = false)
    private String phone;

    @Column(name = "medical_history_summary", columnDefinition = "TEXT")
    private String medicalHistorySummary;
}

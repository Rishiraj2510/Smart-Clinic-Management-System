package com.smartcare.clinicsys.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "user")
public class Doctor {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Specialization is required")
    @Column(nullable = false)
    private String specialization;

    @NotBlank(message = "Phone number is required")
    @Column(nullable = false)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String availability;
}

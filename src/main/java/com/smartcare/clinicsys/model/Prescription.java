package com.smartcare.clinicsys.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "prescriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {

    @Id
    private String id;

    private Long appointmentId;
    private Long patientId;
    private Long doctorId;
    private String patientName;
    private String doctorName;
    private LocalDateTime prescriptionDate;
    private List<String> symptoms;
    private String diagnosis;
    private List<Medication> medications;
    private String instructions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Medication {
        private String name;
        private String dosage;
        private String frequency;
        private String duration;
    }
}

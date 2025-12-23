package com.healthcare.models;

import com.healthcare.models.enums.LabPriority;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lab_orders", indexes = {
        @Index(name = "idx_lab_order_patient", columnList = "patient_id"),
        @Index(name = "idx_lab_order_provider", columnList = "ordering_provider_id"),
        @Index(name = "idx_lab_order_date", columnList = "order_date"),
        @Index(name = "idx_lab_order_test_code", columnList = "test_code")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabOrder extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordering_provider_id", nullable = false)
    private Provider orderingProvider;

    @Column(name = "test_code", nullable = false, length = 50)
    private String testCode;

    @Column(name = "test_name", nullable = false, length = 200)
    private String testName;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    private LabPriority priority;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Column(name = "notes", length = 1000)
    private String notes;

    @OneToMany(mappedBy = "labOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LabResult> results = new ArrayList<>();
}


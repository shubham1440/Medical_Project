package com.healthcare.models;

import com.healthcare.models.enums.AbnormalFlag;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "lab_results", indexes = {
        @Index(name = "idx_lab_result_order", columnList = "lab_order_id"),
        @Index(name = "idx_lab_result_date", columnList = "result_date"),
        @Index(name = "idx_lab_result_abnormal", columnList = "abnormal_flag")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabResult extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_order_id", nullable = false)
    private LabOrder labOrder;

    @Column(name = "result_code", nullable = false, length = 50)
    private String resultCode;

    @Column(name = "result_name", nullable = false, length = 200)
    private String resultName;

    @Column(name = "value", nullable = false, length = 100)
    private String value;

    @Column(name = "unit", length = 50)
    private String unit;

    @Column(name = "reference_range", length = 100)
    private String referenceRange;

    @Enumerated(EnumType.STRING)
    @Column(name = "abnormal_flag", nullable = false, length = 10)
    private AbnormalFlag abnormalFlag;

    @Column(name = "result_date", nullable = false)
    private LocalDate resultDate;

    @Column(name = "report_document", length = 500)
    private String reportDocument;

    @Column(name = "path", length = 500)
    private String path;

    @Column(name = "result_version")
    private Integer resultVersion = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_version_id")
    private LabResult previousVersion;
}

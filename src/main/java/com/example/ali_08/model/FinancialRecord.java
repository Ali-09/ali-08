package com.example.ali_08.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "financial_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "is_recurrent", nullable = false)
    private Boolean isRecurrent = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_type_id")
    private RecordType recordType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_status_id")
    private PaymentStatus paymentStatus;

    @OneToOne(mappedBy = "financialRecord", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private FinancialRecordMetadata metadata;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isRecurrent == null) isRecurrent = false;
    }
}

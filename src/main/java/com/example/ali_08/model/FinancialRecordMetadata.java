package com.example.ali_08.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "financial_record_metadata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialRecordMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financial_record_id", nullable = false, unique = true)
    private FinancialRecord financialRecord;

    @Column(name = "frequency_type", length = 50)
    private String frequencyType; // e.g., "DAILY", "WEEKLY", "MONTHLY", "YEARLY"

    @Column(name = "frequency_value")
    private Integer frequencyValue; // e.g., every 2 weeks

    @Column(name = "next_occurrence")
    private LocalDate nextOccurrence;

    @Column(name = "extra_notes", columnDefinition = "text")
    private String extraNotes;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}

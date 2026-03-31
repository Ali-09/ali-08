package com.example.ali_08.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FinanceUpdateRequest {
    private BigDecimal amount;
    private String description;
    private LocalDate recordDate;
    private Long recordTypeId;
    private Long categoryId;
    private Long paymentMethodId;
    private Long paymentStatusId;
    private Boolean isRecurrent;
    private String frequencyType;
    private Integer frequencyValue;
    private LocalDate nextOccurrence;
    private String extraNotes;
    private Boolean metadataActive;
    private Long dashboardId;
}

package com.example.ali_08.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class FinanceRequest {
    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a 0")
    private BigDecimal amount;

    private String description;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate recordDate;

    @NotNull(message = "El tipo de registro es obligatorio")
    private Long recordTypeId;

    private Long categoryId;
    private Long paymentMethodId;
    private Long paymentStatusId;

    @NotNull(message = "isRecurrent es obligatorio")
    private Boolean isRecurrent;

    private String frequencyType;
    private Integer frequencyValue;
    private LocalDate nextOccurrence;
    private String extraNotes;
    private Boolean metadataActive;
}


package com.example.ali_08.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {
    private Long id;
    private String name;
    private BigDecimal balance;
    private String currencyCode;
    private String type; // Placeholder for now (e.g., Checking, Savings)
}

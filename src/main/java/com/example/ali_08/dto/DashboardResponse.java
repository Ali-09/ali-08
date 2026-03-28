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
public class DashboardResponse {
    private BigDecimal totalBalance;
    private String currencyCode;
    private BigDecimal monthlyIncome; // Sum of income records for month (placeholder for now)
    private BigDecimal monthlyExpenses; // Sum of expense records for month (placeholder for now)
}

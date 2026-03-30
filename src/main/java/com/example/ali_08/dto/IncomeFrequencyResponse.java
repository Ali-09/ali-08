package com.example.ali_08.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IncomeFrequencyResponse {
    private Long id;
    private String name;
    private Integer days;
}


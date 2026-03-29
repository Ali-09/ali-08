package com.example.ali_08.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String name;
    private String email;
    private Double salary;
    private Long currencyId;
    private Long incomeFrequencyId;
    private String incomeFrequencyName;
}

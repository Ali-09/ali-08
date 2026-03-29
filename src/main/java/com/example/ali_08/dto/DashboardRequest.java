package com.example.ali_08.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardRequest {
    @NotBlank(message = "El nombre del dashboard es obligatorio")
    private String name;

    private String description;
}

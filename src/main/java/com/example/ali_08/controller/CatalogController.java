package com.example.ali_08.controller;

import com.example.ali_08.dto.ApiResponse;
import com.example.ali_08.dto.IncomeFrequencyResponse;
import com.example.ali_08.model.IncomeFrequency;
import com.example.ali_08.repository.IncomeFrequencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/catalogs")
@RequiredArgsConstructor
public class CatalogController {

    private final IncomeFrequencyRepository incomeFrequencyRepository;

    @GetMapping("/income-frequencies")
    public ResponseEntity<ApiResponse<List<IncomeFrequencyResponse>>> getIncomeFrequencies() {
        List<IncomeFrequency> frequencies = incomeFrequencyRepository.findAll();

        List<IncomeFrequencyResponse> response = frequencies.stream()
                .map(f -> IncomeFrequencyResponse.builder()
                        .id(f.getId())
                        .name(f.getName())
                        .days(f.getDays())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success(response, "Catálogo de frecuencias obtenido correctamente", HttpStatus.OK.value())
        );
    }
}


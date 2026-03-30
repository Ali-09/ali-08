package com.example.ali_08.controller;

import com.example.ali_08.dto.ApiResponse;
import com.example.ali_08.dto.CategoryResponse;
import com.example.ali_08.dto.IncomeFrequencyResponse;
import com.example.ali_08.model.Category;
import com.example.ali_08.model.IncomeFrequency;
import com.example.ali_08.repository.CategoryRepository;
import com.example.ali_08.repository.IncomeFrequencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/catalogs")
@RequiredArgsConstructor
public class CatalogController {

    private final IncomeFrequencyRepository incomeFrequencyRepository;
    private final CategoryRepository categoryRepository;

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

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories(
            @RequestParam(required = false) String recordType
    ) {
        List<Category> categories = categoryRepository.findAll();

        if (recordType != null && !recordType.isBlank()) {
            categories = categories.stream()
                    .filter(c -> c.getRecordType() != null && c.getRecordType().getName() != null)
                    .filter(c -> c.getRecordType().getName().equalsIgnoreCase(recordType))
                    .collect(Collectors.toList());
        }

        List<CategoryResponse> response = categories.stream()
                .map(c -> CategoryResponse.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .recordTypeId(c.getRecordType() != null ? c.getRecordType().getId() : null)
                        .recordTypeName(c.getRecordType() != null ? c.getRecordType().getName() : null)
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success(response, "Catálogo de categorías obtenido correctamente", HttpStatus.OK.value())
        );
    }
}

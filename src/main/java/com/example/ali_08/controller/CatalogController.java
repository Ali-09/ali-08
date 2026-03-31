package com.example.ali_08.controller;

import com.example.ali_08.dto.ApiResponse;
import com.example.ali_08.dto.CategoryResponse;
import com.example.ali_08.dto.IncomeFrequencyResponse;
import com.example.ali_08.dto.PaymentMethodResponse;
import com.example.ali_08.dto.PaymentStatusResponse;
import com.example.ali_08.dto.RecordTypeResponse;
import com.example.ali_08.model.Category;
import com.example.ali_08.model.IncomeFrequency;
import com.example.ali_08.model.PaymentMethod;
import com.example.ali_08.model.PaymentStatus;
import com.example.ali_08.model.RecordType;
import com.example.ali_08.repository.CategoryRepository;
import com.example.ali_08.repository.IncomeFrequencyRepository;
import com.example.ali_08.repository.PaymentMethodRepository;
import com.example.ali_08.repository.PaymentStatusRepository;
import com.example.ali_08.repository.RecordTypeRepository;
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
    private final RecordTypeRepository recordTypeRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentStatusRepository paymentStatusRepository;

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
            @RequestParam(required = false) Long recordTypeId,
            @RequestParam(required = false) String recordType
    ) {
        List<Category> categories = categoryRepository.findAll();

        if (recordTypeId != null) {
            categories = categories.stream()
                    .filter(c -> c.getRecordType() != null && c.getRecordType().getId() != null)
                    .filter(c -> c.getRecordType().getId().equals(recordTypeId))
                    .collect(Collectors.toList());
        }

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

    @GetMapping("/record-types")
    public ResponseEntity<ApiResponse<List<RecordTypeResponse>>> getRecordTypes() {
        List<RecordType> types = recordTypeRepository.findAll();

        List<RecordTypeResponse> response = types.stream()
                .map(t -> RecordTypeResponse.builder()
                        .id(t.getId())
                        .name(t.getName())
                        .description(t.getDescription())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success(response, "Catálogo de tipos obtenido correctamente", HttpStatus.OK.value())
        );
    }

    @GetMapping("/payment-methods")
    public ResponseEntity<ApiResponse<List<PaymentMethodResponse>>> getPaymentMethods() {
        List<PaymentMethod> methods = paymentMethodRepository.findAll();

        List<PaymentMethodResponse> response = methods.stream()
                .map(m -> PaymentMethodResponse.builder()
                        .id(m.getId())
                        .name(m.getName())
                        .isActive(m.getIsActive())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success(response, "Catálogo de métodos de pago obtenido correctamente", HttpStatus.OK.value())
        );
    }

    @GetMapping("/payment-statuses")
    public ResponseEntity<ApiResponse<List<PaymentStatusResponse>>> getPaymentStatuses() {
        List<PaymentStatus> statuses = paymentStatusRepository.findAll();

        List<PaymentStatusResponse> response = statuses.stream()
                .map(s -> PaymentStatusResponse.builder()
                        .id(s.getId())
                        .status(s.getStatus())
                        .color(s.getColor())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success(response, "Catálogo de estatus de pago obtenido correctamente", HttpStatus.OK.value())
        );
    }
}

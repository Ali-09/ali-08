package com.example.ali_08.controller;

import com.example.ali_08.dto.ApiResponse;
import com.example.ali_08.dto.FinanceRequest;
import com.example.ali_08.dto.FinanceResponse;
import com.example.ali_08.dto.FinanceUpdateRequest;
import com.example.ali_08.service.FinanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/finances")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService financeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<FinanceResponse>>> getFinances(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long dashboardId
    ) {
        if ((startDate == null) != (endDate == null)) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error("Debes enviar startDate y endDate juntos", HttpStatus.BAD_REQUEST.value())
            );
        }

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error("La fecha de inicio no puede ser posterior a la fecha de fin", HttpStatus.BAD_REQUEST.value())
            );
        }

        if (startDate != null && endDate != null) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
            if (days > 366) {
                return ResponseEntity.badRequest().body(
                    ApiResponse.error("El rango máximo permitido es de 12 meses", HttpStatus.BAD_REQUEST.value())
                );
            }
        }

        List<FinanceResponse> finances = financeService.getFinancesByPeriod(startDate, endDate, dashboardId);
        return ResponseEntity.ok(
            ApiResponse.success(finances, "Finanzas obtenidas correctamente", HttpStatus.OK.value())
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FinanceResponse>> createFinance(@Valid @RequestBody FinanceRequest request) {
        FinanceResponse created = financeService.createFinance(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(created, "Finance creada correctamente", HttpStatus.CREATED.value())
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<FinanceResponse>> updateFinance(@PathVariable Long id, @RequestBody FinanceUpdateRequest request) {
        FinanceResponse updated = financeService.updateFinance(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(updated, "Finance actualizada correctamente", HttpStatus.OK.value())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFinance(@PathVariable Long id) {
        financeService.deleteFinance(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Finance eliminada correctamente", HttpStatus.OK.value())
        );
    }
}

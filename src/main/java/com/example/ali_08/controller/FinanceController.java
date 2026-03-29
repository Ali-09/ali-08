package com.example.ali_08.controller;

import com.example.ali_08.dto.ApiResponse;
import com.example.ali_08.dto.FinanceResponse;
import com.example.ali_08.service.FinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        // Validar rango de fechas si se proporcionan
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error("La fecha de inicio no puede ser posterior a la fecha de fin", HttpStatus.BAD_REQUEST.value())
            );
        }

        List<FinanceResponse> finances = financeService.getFinancesByPeriod(startDate, endDate);
        return ResponseEntity.ok(
            ApiResponse.success(finances, "Finanzas obtenidas correctamente", HttpStatus.OK.value())
        );
    }
}

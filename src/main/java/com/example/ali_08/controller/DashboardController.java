package com.example.ali_08.controller;

import com.example.ali_08.dto.ApiResponse;
import com.example.ali_08.dto.DashboardResponse;
import com.example.ali_08.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboards")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {
        DashboardResponse dashboardData = dashboardService.getDashboardData();
        return ResponseEntity.ok(
            ApiResponse.success(dashboardData, "Datos del dashboard obtenidos correctamente", HttpStatus.OK.value())
        );
    }
}

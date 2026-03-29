package com.example.ali_08.controller;

import com.example.ali_08.dto.ApiResponse;
import com.example.ali_08.dto.DashboardRequest;
import com.example.ali_08.dto.DashboardResponse;
import com.example.ali_08.service.DashboardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboards")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DashboardResponse>>> getDashboard() {
        List<DashboardResponse> dashboardData = dashboardService.getDashboardData();
        return ResponseEntity.ok(
            ApiResponse.success(dashboardData, "Lista de dashboards obtenida correctamente", HttpStatus.OK.value())
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DashboardResponse>> createDashboard(@Valid @RequestBody DashboardRequest request) {
        DashboardResponse dashboard = dashboardService.createDashboard(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success(dashboard, "Dashboard creado correctamente", HttpStatus.CREATED.value())
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<DashboardResponse>> updateDashboard(@PathVariable Long id, @Valid @RequestBody DashboardRequest request) {
        DashboardResponse dashboard = dashboardService.updateDashboard(id, request);
        return ResponseEntity.ok(
            ApiResponse.success(dashboard, "Dashboard actualizado correctamente", HttpStatus.OK.value())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDashboard(@PathVariable Long id) {
        dashboardService.deleteDashboard(id);
        return ResponseEntity.ok(
            ApiResponse.success(null, "Dashboard eliminado correctamente", HttpStatus.OK.value())
        );
    }
}

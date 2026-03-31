package com.example.ali_08.service;

import com.example.ali_08.dto.DashboardRequest;
import com.example.ali_08.dto.DashboardResponse;
import com.example.ali_08.dto.FinanceResponse;
import com.example.ali_08.model.Dashboard;
import com.example.ali_08.model.User;
import com.example.ali_08.model.UserProfile;
import com.example.ali_08.repository.DashboardRepository;
import com.example.ali_08.repository.UserProfileRepository;
import com.example.ali_08.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final FinanceService financeService;

    public List<DashboardResponse> getDashboardData() {
        User user = getCurrentUser();
        
        // 1. Obtener dashboards del usuario de la BD
        List<Dashboard> userDashboards = dashboardRepository.findByUser(user);

        // 2. Calcular ingresos y gastos por dashboard (incluye recurrentes)
        LocalDateTime now = LocalDateTime.now();
        LocalDate startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate();
        LocalDate endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate();

        // 3. Mapear cada dashboard de la BD a la respuesta
        return userDashboards.stream()
                .map(db -> {
                    List<FinanceResponse> dashboardFinances = financeService.getFinancesByPeriod(startOfMonth, endOfMonth, db.getId());

                    BigDecimal monthlyIncome = dashboardFinances.stream()
                            .filter(f -> "Ingreso".equalsIgnoreCase(f.getRecordTypeName()))
                            .map(FinanceResponse::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal monthlyExpenses = dashboardFinances.stream()
                            .filter(f -> "Gasto".equalsIgnoreCase(f.getRecordTypeName()))
                            .map(FinanceResponse::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal currentBalance = monthlyIncome.subtract(monthlyExpenses);

                    return mapToResponse(db, currentBalance, monthlyIncome, monthlyExpenses);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public DashboardResponse createDashboard(DashboardRequest request) {
        User user = getCurrentUser();
        
        Dashboard dashboard = Dashboard.builder()
                .name(request.getName())
                .description(request.getDescription())
                .user(user)
                .build();
        
        dashboard = dashboardRepository.save(dashboard);
        
        // Obtenemos métricas para la respuesta
        return getSingleDashboardResponse(dashboard);
    }

    @Transactional
    public DashboardResponse updateDashboard(Long id, DashboardRequest request) {
        Dashboard dashboard = dashboardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dashboard no encontrado"));
        
        User user = getCurrentUser();
        if (!dashboard.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No tienes permiso para editar este dashboard");
        }

        dashboard.setName(request.getName());
        dashboard.setDescription(request.getDescription());
        
        dashboard = dashboardRepository.save(dashboard);
        
        return getSingleDashboardResponse(dashboard);
    }

    @Transactional
    public void deleteDashboard(Long id) {
        Dashboard dashboard = dashboardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dashboard no encontrado"));
        
        User user = getCurrentUser();
        if (!dashboard.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No tienes permiso para eliminar este dashboard");
        }

        dashboardRepository.delete(dashboard);
    }

    private DashboardResponse getSingleDashboardResponse(Dashboard db) {
        User user = db.getUser();
        userProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Perfil de usuario no encontrado"));

        LocalDateTime now = LocalDateTime.now();
        LocalDate startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate();
        LocalDate endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate();

        List<FinanceResponse> dashboardFinances = financeService.getFinancesByPeriod(startOfMonth, endOfMonth, db.getId());

        BigDecimal monthlyIncome = dashboardFinances.stream()
                .filter(f -> "Ingreso".equalsIgnoreCase(f.getRecordTypeName()))
                .map(FinanceResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal monthlyExpenses = dashboardFinances.stream()
                .filter(f -> "Gasto".equalsIgnoreCase(f.getRecordTypeName()))
                .map(FinanceResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal currentBalance = monthlyIncome.subtract(monthlyExpenses);

        return mapToResponse(db, currentBalance, monthlyIncome, monthlyExpenses);
    }

    private DashboardResponse mapToResponse(Dashboard db, BigDecimal currentBalance, BigDecimal monthlyIncome, BigDecimal monthlyExpenses) {
        return DashboardResponse.builder()
                .id(db.getId())
                .name(db.getName())
                .description(db.getDescription())
                .totalBalance(currentBalance)
                .currencyCode("USD")
                .monthlyIncome(monthlyIncome)
                .monthlyExpenses(monthlyExpenses)
                .build();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}

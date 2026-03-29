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
        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Perfil de usuario no encontrado"));

        BigDecimal salary = profile.getSalary() != null ? profile.getSalary() : BigDecimal.ZERO;
        
        // 1. Obtener dashboards del usuario de la BD
        List<Dashboard> userDashboards = dashboardRepository.findByUser(user);

        // 2. Calcular gastos del mes actual usando la nueva lógica de finanzas (incluye recurrentes)
        LocalDateTime now = LocalDateTime.now();
        LocalDate startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate();
        LocalDate endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate();
        
        List<FinanceResponse> currentMonthFinances = financeService.getFinancesByPeriod(startOfMonth, endOfMonth);
        
        BigDecimal monthlyExpenses = currentMonthFinances.stream()
                .filter(f -> f.getRecordTypeName().equalsIgnoreCase("Gasto"))
                .map(FinanceResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // El balance es Salario - Gastos del mes
        BigDecimal currentBalance = salary.subtract(monthlyExpenses);

        // 3. Mapear cada dashboard de la BD a la respuesta
        return userDashboards.stream()
                .map(db -> mapToResponse(db, currentBalance, salary, monthlyExpenses))
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
        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Perfil de usuario no encontrado"));

        BigDecimal salary = profile.getSalary() != null ? profile.getSalary() : BigDecimal.ZERO;

        LocalDateTime now = LocalDateTime.now();
        LocalDate startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate();
        LocalDate endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate();

        List<FinanceResponse> currentMonthFinances = financeService.getFinancesByPeriod(startOfMonth, endOfMonth);

        BigDecimal monthlyExpenses = currentMonthFinances.stream()
                .filter(f -> f.getRecordTypeName().equalsIgnoreCase("Gasto"))
                .map(FinanceResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal currentBalance = salary.subtract(monthlyExpenses);

        return mapToResponse(db, currentBalance, salary, monthlyExpenses);
    }

    private DashboardResponse mapToResponse(Dashboard db, BigDecimal currentBalance, BigDecimal salary, BigDecimal monthlyExpenses) {
        return DashboardResponse.builder()
                .id(db.getId())
                .name(db.getName())
                .description(db.getDescription())
                .totalBalance(currentBalance)
                .currencyCode("USD")
                .monthlyIncome(salary)
                .monthlyExpenses(monthlyExpenses)
                .build();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}

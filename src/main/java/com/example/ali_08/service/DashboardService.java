package com.example.ali_08.service;

import com.example.ali_08.dto.DashboardResponse;
import com.example.ali_08.model.Dashboard;
import com.example.ali_08.model.Record;
import com.example.ali_08.model.User;
import com.example.ali_08.repository.DashboardRepository;
import com.example.ali_08.repository.RecordRepository;
import com.example.ali_08.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final RecordRepository recordRepository;
    private final UserRepository userRepository;

    public List<DashboardResponse> getDashboardData() {
        User user = getCurrentUser();
        
        // 1. Obtener dashboards del usuario de la BD
        List<Dashboard> userDashboards = dashboardRepository.findByUser(user);

        // 2. Calcular métricas financieras globales del usuario
        List<Record> allRecords = recordRepository.findByUserOrderByDateDesc(user);
        BigDecimal totalIncome = calculateSum(allRecords, "Ingreso");
        BigDecimal totalExpenses = calculateSum(allRecords, "Gasto");
        BigDecimal totalBalance = totalIncome.subtract(totalExpenses);

        LocalDateTime startOfMonth = LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = LocalDateTime.now().with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);
        List<Record> currentMonthRecords = recordRepository.findByUserAndDateBetween(user, startOfMonth, endOfMonth);
        
        BigDecimal monthlyIncome = calculateSum(currentMonthRecords, "Ingreso");
        BigDecimal monthlyExpenses = calculateSum(currentMonthRecords, "Gasto");

        // 3. Mapear cada dashboard de la BD a la respuesta
        return userDashboards.stream()
                .map(db -> DashboardResponse.builder()
                        .id(db.getId())
                        .name(db.getName())
                        .description(db.getDescription())
                        .totalBalance(totalBalance)
                        .currencyCode("USD")
                        .monthlyIncome(monthlyIncome)
                        .monthlyExpenses(monthlyExpenses)
                        .build())
                .collect(Collectors.toList());
    }

    private BigDecimal calculateSum(List<Record> records, String type) {
        return records.stream()
                .filter(r -> r.getCategory().getRecordType().getName().equalsIgnoreCase(type))
                .map(Record::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}

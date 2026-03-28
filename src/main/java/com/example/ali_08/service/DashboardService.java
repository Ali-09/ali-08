package com.example.ali_08.service;

import com.example.ali_08.dto.DashboardResponse;
import com.example.ali_08.model.Record;
import com.example.ali_08.model.User;
import com.example.ali_08.repository.AccountRepository;
import com.example.ali_08.repository.RecordRepository;
import com.example.ali_08.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final RecordRepository recordRepository;
    private final UserRepository userRepository;

    public List<DashboardResponse> getDashboardData() {
        User user = getCurrentUser();
        List<DashboardResponse> dashboards = new ArrayList<>();

        // 1. Resumen Global
        List<Record> allRecords = recordRepository.findByUserOrderByDateDesc(user);
        BigDecimal totalIncome = calculateSum(allRecords, "Ingreso");
        BigDecimal totalExpenses = calculateSum(allRecords, "Gasto");
        
        dashboards.add(DashboardResponse.builder()
                .title("Resumen Global")
                .totalBalance(totalIncome.subtract(totalExpenses))
                .currencyCode("USD")
                .monthlyIncome(totalIncome)
                .monthlyExpenses(totalExpenses)
                .build());

        // 2. Mes Actual
        LocalDateTime startOfMonth = LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = LocalDateTime.now().with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);
        List<Record> currentMonthRecords = recordRepository.findByUserAndDateBetween(user, startOfMonth, endOfMonth);
        
        dashboards.add(DashboardResponse.builder()
                .title("Mes Actual")
                .totalBalance(null) // No aplica balance total para un mes específico
                .currencyCode("USD")
                .monthlyIncome(calculateSum(currentMonthRecords, "Ingreso"))
                .monthlyExpenses(calculateSum(currentMonthRecords, "Gasto"))
                .build());

        // 3. Mes Pasado
        LocalDateTime startOfLastMonth = startOfMonth.minusMonths(1);
        LocalDateTime endOfLastMonth = startOfLastMonth.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);
        List<Record> lastMonthRecords = recordRepository.findByUserAndDateBetween(user, startOfLastMonth, endOfLastMonth);

        dashboards.add(DashboardResponse.builder()
                .title("Mes Pasado")
                .totalBalance(null)
                .currencyCode("USD")
                .monthlyIncome(calculateSum(lastMonthRecords, "Ingreso"))
                .monthlyExpenses(calculateSum(lastMonthRecords, "Gasto"))
                .build());

        return dashboards;
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

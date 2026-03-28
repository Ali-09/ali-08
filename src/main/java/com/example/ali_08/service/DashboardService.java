package com.example.ali_08.service;

import com.example.ali_08.dto.DashboardResponse;
import com.example.ali_08.model.Account;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final AccountRepository accountRepository;
    private final RecordRepository recordRepository;
    private final UserRepository userRepository;

    public DashboardResponse getDashboardData() {
        User user = getCurrentUser();

        // Obtener todos los movimientos del usuario para calcular el balance virtual
        List<Record> allRecords = recordRepository.findByUserOrderByDateDesc(user);

        BigDecimal totalIncome = allRecords.stream()
                .filter(r -> r.getCategory().getRecordType().getName().equalsIgnoreCase("Ingreso"))
                .map(Record::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = allRecords.stream()
                .filter(r -> r.getCategory().getRecordType().getName().equalsIgnoreCase("Gasto"))
                .map(Record::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalBalance = totalIncome.subtract(totalExpenses);

        // Calcular ingresos y gastos del mes actual
        LocalDateTime startOfMonth = LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = LocalDateTime.now().with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);

        List<Record> monthlyRecords = recordRepository.findByUserAndDateBetween(user, startOfMonth, endOfMonth);

        BigDecimal monthlyIncome = monthlyRecords.stream()
                .filter(r -> r.getCategory().getRecordType().getName().equalsIgnoreCase("Ingreso"))
                .map(Record::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal monthlyExpenses = monthlyRecords.stream()
                .filter(r -> r.getCategory().getRecordType().getName().equalsIgnoreCase("Gasto"))
                .map(Record::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return DashboardResponse.builder()
                .totalBalance(totalBalance)
                .currencyCode("USD") // Default currency
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

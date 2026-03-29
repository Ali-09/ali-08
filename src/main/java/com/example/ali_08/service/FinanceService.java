package com.example.ali_08.service;

import com.example.ali_08.dto.FinanceResponse;
import com.example.ali_08.model.*;
import com.example.ali_08.repository.FinancialRecordRepository;
import com.example.ali_08.repository.UserProfileRepository;
import com.example.ali_08.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final FinancialRecordRepository financialRecordRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    public List<FinanceResponse> getFinancesByPeriod(LocalDate startDate, LocalDate endDate) {
        User user = getCurrentUser();
        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado"));

        // Definir periodo por defecto si no se proporciona
        if (startDate == null || endDate == null) {
            IncomeFrequency frequency = profile.getIncomeFrequency();
            if (frequency != null && frequency.getName().equalsIgnoreCase("Mensual")) {
                startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
                endDate = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
            } else if (frequency != null && frequency.getName().equalsIgnoreCase("Quincenal")) {
                int dayOfMonth = LocalDate.now().getDayOfMonth();
                if (dayOfMonth <= 15) {
                    startDate = LocalDate.now().withDayOfMonth(1);
                    endDate = LocalDate.now().withDayOfMonth(15);
                } else {
                    startDate = LocalDate.now().withDayOfMonth(16);
                    endDate = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
                }
            } else if (frequency != null && frequency.getName().equalsIgnoreCase("Semanal")) {
                startDate = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
                endDate = startDate.plusDays(6);
            } else {
                // Por defecto: Mes actual
                startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
                endDate = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
            }
        }

        // 1. Obtener registros no recurrentes en el rango
        List<FinancialRecord> records = financialRecordRepository.findByUserAndRecordDateBetween(user, startDate, endDate);

        // 2. Obtener registros recurrentes y filtrar los que caen en el rango
        List<FinancialRecord> recurrentRecords = financialRecordRepository.findByUserAndIsRecurrentTrue(user);
        
        List<FinanceResponse> responses = new ArrayList<>();
        
        // Agregar registros normales
        for (FinancialRecord record : records) {
            if (!record.getIsRecurrent()) {
                responses.add(mapToResponse(record, record.getRecordDate()));
            }
        }

        // Lógica de recurrencia
        for (FinancialRecord recurrent : recurrentRecords) {
            List<LocalDate> occurrences = calculateOccurrences(recurrent, startDate, endDate);
            for (LocalDate occurrenceDate : occurrences) {
                responses.add(mapToResponse(recurrent, occurrenceDate));
            }
        }

        return responses;
    }

    private List<LocalDate> calculateOccurrences(FinancialRecord record, LocalDate start, LocalDate end) {
        List<LocalDate> dates = new ArrayList<>();
        FinancialRecordMetadata metadata = record.getMetadata();
        if (metadata == null || !metadata.getIsActive()) return dates;

        LocalDate current = record.getRecordDate();
        String type = metadata.getFrequencyType(); // DAILY, WEEKLY, MONTHLY
        int value = metadata.getFrequencyValue() != null ? metadata.getFrequencyValue() : 1;

        // Avanzar hasta que estemos dentro o después del rango de inicio
        while (current.isBefore(start)) {
            current = advanceDate(current, type, value);
        }

        // Agregar mientras estemos dentro del rango de fin
        while (!current.isAfter(end)) {
            dates.add(current);
            current = advanceDate(current, type, value);
        }

        return dates;
    }

    private LocalDate advanceDate(LocalDate date, String type, int value) {
        if (type == null) return date.plusMonths(1);
        return switch (type.toUpperCase()) {
            case "DAILY" -> date.plusDays(value);
            case "WEEKLY" -> date.plusWeeks(value);
            case "MONTHLY" -> date.plusMonths(value);
            case "YEARLY" -> date.plusYears(value);
            default -> date.plusMonths(1);
        };
    }

    private FinanceResponse mapToResponse(FinancialRecord record, LocalDate date) {
        return FinanceResponse.builder()
                .id(record.getId())
                .description(record.getDescription())
                .amount(record.getAmount())
                .date(date)
                .categoryName(record.getCategory() != null ? record.getCategory().getName() : "Sin categoría")
                .recordTypeName(record.getRecordType() != null ? record.getRecordType().getName() : "Desconocido")
                .isRecurrent(record.getIsRecurrent())
                .frequencyType(record.getMetadata() != null ? record.getMetadata().getFrequencyType() : null)
                .build();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}

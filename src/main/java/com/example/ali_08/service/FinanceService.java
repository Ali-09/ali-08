package com.example.ali_08.service;

import com.example.ali_08.dto.FinanceResponse;
import com.example.ali_08.dto.FinanceRequest;
import com.example.ali_08.dto.FinanceUpdateRequest;
import com.example.ali_08.model.*;
import com.example.ali_08.repository.FinancialRecordRepository;
import com.example.ali_08.repository.FinancialRecordMetadataRepository;
import com.example.ali_08.repository.CategoryRepository;
import com.example.ali_08.repository.RecordTypeRepository;
import com.example.ali_08.repository.PaymentMethodRepository;
import com.example.ali_08.repository.PaymentStatusRepository;
import com.example.ali_08.repository.DashboardRepository;
import com.example.ali_08.repository.UserProfileRepository;
import com.example.ali_08.repository.UserRepository;
import jakarta.transaction.Transactional;
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
    private final FinancialRecordMetadataRepository financialRecordMetadataRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final RecordTypeRepository recordTypeRepository;
    private final CategoryRepository categoryRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentStatusRepository paymentStatusRepository;
    private final DashboardRepository dashboardRepository;

    public List<FinanceResponse> getFinancesByPeriod(LocalDate startDate, LocalDate endDate, Long dashboardId) {
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
        
        if (dashboardId != null) {
            records = records.stream()
                    .filter(r -> r.getDashboard() != null && r.getDashboard().getId().equals(dashboardId))
                    .collect(Collectors.toList());
            recurrentRecords = recurrentRecords.stream()
                    .filter(r -> r.getDashboard() != null && r.getDashboard().getId().equals(dashboardId))
                    .collect(Collectors.toList());
        }

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

    @Transactional
    public FinanceResponse createFinance(FinanceRequest request) {
        User user = getCurrentUser();

        RecordType recordType = recordTypeRepository.findById(request.getRecordTypeId())
                .orElseThrow(() -> new RuntimeException("Tipo de registro no encontrado"));

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        }

        PaymentMethod paymentMethod = null;
        if (request.getPaymentMethodId() != null) {
            paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId())
                    .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));
        }

        PaymentStatus paymentStatus = null;
        if (request.getPaymentStatusId() != null) {
            paymentStatus = paymentStatusRepository.findById(request.getPaymentStatusId())
                    .orElseThrow(() -> new RuntimeException("Estatus de pago no encontrado"));
        }

        FinancialRecord record = FinancialRecord.builder()
                .user(user)
                .amount(request.getAmount())
                .description(request.getDescription())
                .recordDate(request.getRecordDate())
                .isRecurrent(request.getIsRecurrent())
                .recordType(recordType)
                .category(category)
                .paymentMethod(paymentMethod)
                .paymentStatus(paymentStatus)
                .build();

        if (request.getDashboardId() != null) {
            Dashboard dash = dashboardRepository.findById(request.getDashboardId())
                    .orElseThrow(() -> new RuntimeException("Dashboard no encontrado"));
            if (!dash.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("No tienes permiso para asociar finanzas a este dashboard");
            }
            record.setDashboard(dash);
        }

        record = financialRecordRepository.save(record);

        if (Boolean.TRUE.equals(request.getIsRecurrent())) {
            if (request.getFrequencyType() == null || request.getFrequencyType().isBlank()) {
                throw new RuntimeException("frequencyType es obligatorio para registros recurrentes");
            }

            FinancialRecordMetadata metadata = FinancialRecordMetadata.builder()
                    .financialRecord(record)
                    .frequencyType(request.getFrequencyType())
                    .frequencyValue(request.getFrequencyValue() != null ? request.getFrequencyValue() : 1)
                    .nextOccurrence(request.getNextOccurrence())
                    .extraNotes(request.getExtraNotes())
                    .isActive(request.getMetadataActive() != null ? request.getMetadataActive() : true)
                    .build();
            financialRecordMetadataRepository.save(metadata);
            record.setMetadata(metadata);
        }

        return mapToResponse(record, record.getRecordDate());
    }

    @Transactional
    public FinanceResponse updateFinance(Long id, FinanceUpdateRequest request) {
        User user = getCurrentUser();

        FinancialRecord record = financialRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Finance no encontrada"));

        if (!record.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No tienes permiso para editar esta finance");
        }

        if (request.getAmount() != null) record.setAmount(request.getAmount());
        if (request.getDescription() != null) record.setDescription(request.getDescription());
        if (request.getRecordDate() != null) record.setRecordDate(request.getRecordDate());

        if (request.getRecordTypeId() != null) {
            RecordType recordType = recordTypeRepository.findById(request.getRecordTypeId())
                    .orElseThrow(() -> new RuntimeException("Tipo de registro no encontrado"));
            record.setRecordType(recordType);
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            record.setCategory(category);
        }

        if (request.getPaymentMethodId() != null) {
            PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId())
                    .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));
            record.setPaymentMethod(paymentMethod);
        }

        if (request.getPaymentStatusId() != null) {
            PaymentStatus paymentStatus = paymentStatusRepository.findById(request.getPaymentStatusId())
                    .orElseThrow(() -> new RuntimeException("Estatus de pago no encontrado"));
            record.setPaymentStatus(paymentStatus);
        }

        if (request.getDashboardId() != null) {
            Dashboard dash = dashboardRepository.findById(request.getDashboardId())
                    .orElseThrow(() -> new RuntimeException("Dashboard no encontrado"));
            if (!dash.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("No tienes permiso para asociar finanzas a este dashboard");
            }
            record.setDashboard(dash);
        }

        if (request.getIsRecurrent() != null) {
            record.setIsRecurrent(request.getIsRecurrent());
        }

        FinancialRecordMetadata metadata = record.getMetadata();
        if (Boolean.TRUE.equals(record.getIsRecurrent())) {
            if (metadata == null) {
                metadata = FinancialRecordMetadata.builder()
                        .financialRecord(record)
                        .isActive(true)
                        .frequencyValue(1)
                        .build();
            }

            if (request.getFrequencyType() != null) metadata.setFrequencyType(request.getFrequencyType());
            if (request.getFrequencyValue() != null) metadata.setFrequencyValue(request.getFrequencyValue());
            if (request.getNextOccurrence() != null) metadata.setNextOccurrence(request.getNextOccurrence());
            if (request.getExtraNotes() != null) metadata.setExtraNotes(request.getExtraNotes());
            if (request.getMetadataActive() != null) metadata.setIsActive(request.getMetadataActive());

            if (metadata.getFrequencyType() == null || metadata.getFrequencyType().isBlank()) {
                throw new RuntimeException("frequencyType es obligatorio para registros recurrentes");
            }

            FinancialRecordMetadata saved = financialRecordMetadataRepository.save(metadata);
            record.setMetadata(saved);
        } else {
            if (metadata != null) {
                financialRecordMetadataRepository.delete(metadata);
                record.setMetadata(null);
            }
        }

        FinancialRecord saved = financialRecordRepository.save(record);
        return mapToResponse(saved, saved.getRecordDate());
    }

    @Transactional
    public void deleteFinance(Long id) {
        User user = getCurrentUser();
        FinancialRecord record = financialRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Finance no encontrada"));

        if (!record.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No tienes permiso para eliminar esta finance");
        }

        financialRecordRepository.delete(record);
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
                .dashboardId(record.getDashboard() != null ? record.getDashboard().getId() : null)
                .build();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}

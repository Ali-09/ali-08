package com.example.ali_08.service;

import com.example.ali_08.dto.RecordRequest;
import com.example.ali_08.model.User;
import com.example.ali_08.model.Category;
import com.example.ali_08.model.Record;
import com.example.ali_08.repository.CategoryRepository;
import com.example.ali_08.repository.RecordRepository;
import com.example.ali_08.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public List<com.example.ali_08.model.Record> getAllUserRecords() {
        User user = getCurrentUser();
        return recordRepository.findByUserOrderByDateDesc(user);
    }

    @Transactional
    public com.example.ali_08.model.Record createRecord(RecordRequest request) {
        User user = getCurrentUser();

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        com.example.ali_08.model.Record record = com.example.ali_08.model.Record.builder()
                .description(request.getDescription())
                .amount(request.getAmount())
                .date(request.getDate() != null ? request.getDate() : java.time.LocalDateTime.now())
                .category(category)
                .user(user)
                .build();

        return recordRepository.save(record);
    }

    @Transactional
    public com.example.ali_08.model.Record updateRecord(Long id, RecordRequest request) {
        com.example.ali_08.model.Record record = recordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));
        
        User user = getCurrentUser();
        if (!record.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No tienes permiso para editar este movimiento");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        record.setDescription(request.getDescription());
        record.setAmount(request.getAmount());
        record.setDate(request.getDate() != null ? request.getDate() : record.getDate());
        record.setCategory(category);

        return recordRepository.save(record);
    }

    @Transactional
    public void deleteRecord(Long id) {
        com.example.ali_08.model.Record record = recordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));
        
        User user = getCurrentUser();
        if (!record.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No tienes permiso para eliminar este movimiento");
        }

        recordRepository.delete(record);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}

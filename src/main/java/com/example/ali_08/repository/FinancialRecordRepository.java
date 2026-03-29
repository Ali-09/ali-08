package com.example.ali_08.repository;

import com.example.ali_08.model.FinancialRecord;
import com.example.ali_08.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {
    List<FinancialRecord> findByUserAndRecordDateBetween(User user, LocalDate start, LocalDate end);
    List<FinancialRecord> findByUserAndIsRecurrentTrue(User user);
}

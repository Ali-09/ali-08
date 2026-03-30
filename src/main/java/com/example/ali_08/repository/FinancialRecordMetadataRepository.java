package com.example.ali_08.repository;

import com.example.ali_08.model.FinancialRecord;
import com.example.ali_08.model.FinancialRecordMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FinancialRecordMetadataRepository extends JpaRepository<FinancialRecordMetadata, Long> {
    Optional<FinancialRecordMetadata> findByFinancialRecord(FinancialRecord financialRecord);
}


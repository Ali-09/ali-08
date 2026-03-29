package com.example.ali_08.repository;

import com.example.ali_08.model.IncomeFrequency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IncomeFrequencyRepository extends JpaRepository<IncomeFrequency, Long> {
    Optional<IncomeFrequency> findByName(String name);
}

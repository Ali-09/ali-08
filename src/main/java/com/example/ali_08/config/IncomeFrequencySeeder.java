package com.example.ali_08.config;

import com.example.ali_08.model.IncomeFrequency;
import com.example.ali_08.repository.IncomeFrequencyRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class IncomeFrequencySeeder {

    private final IncomeFrequencyRepository incomeFrequencyRepository;

    @PostConstruct
    public void seed() {
        if (incomeFrequencyRepository.count() == 0) {
            List<IncomeFrequency> frequencies = Arrays.asList(
                    IncomeFrequency.builder().name("Semanal").days(7).build(),
                    IncomeFrequency.builder().name("Quincenal").days(15).build(),
                    IncomeFrequency.builder().name("Mensual").days(30).build()
            );

            incomeFrequencyRepository.saveAll(frequencies);
        }
    }
}


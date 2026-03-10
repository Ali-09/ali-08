package com.example.ali_08.config;

import com.example.ali_08.model.Category;
import com.example.ali_08.model.RecordType;
import com.example.ali_08.repository.CategoryRepository;
import com.example.ali_08.repository.RecordTypeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@DependsOn("recordTypeSeeder")
public class CategorySeeder {

    private final CategoryRepository categoryRepository;
    private final RecordTypeRepository recordTypeRepository;

    @PostConstruct
    public void seed() {
        if (categoryRepository.count() == 0) {
            RecordType income = recordTypeRepository.findByName("Ingreso")
                    .orElseThrow(() -> new RuntimeException("RecordType Ingreso not found"));
            RecordType expense = recordTypeRepository.findByName("Gasto")
                    .orElseThrow(() -> new RuntimeException("RecordType Gasto not found"));

            List<Category> categories = Arrays.asList(
                    Category.builder().name("Salario").recordType(income).build(),
                    Category.builder().name("Freelance").recordType(income).build(),
                    Category.builder().name("Inversiones").recordType(income).build(),
                    Category.builder().name("Comida y Restaurantes").recordType(expense).build(),
                    Category.builder().name("Transporte").recordType(expense).build(),
                    Category.builder().name("Servicios").recordType(expense).build(),
                    Category.builder().name("Entretenimiento").recordType(expense).build(),
                    Category.builder().name("Compras").recordType(expense).build(),
                    Category.builder().name("Salud").recordType(expense).build(),
                    Category.builder().name("Educación").recordType(expense).build()
            );

            categoryRepository.saveAll(categories);
        }
    }
}

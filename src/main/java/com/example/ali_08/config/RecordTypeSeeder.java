package com.example.ali_08.config;

import com.example.ali_08.model.RecordType;
import com.example.ali_08.repository.RecordTypeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RecordTypeSeeder {

    private final RecordTypeRepository recordTypeRepository;

    @PostConstruct
    public void seed() {
        if (recordTypeRepository.count() == 0) {
            List<RecordType> types = Arrays.asList(
                    RecordType.builder().name("Ingreso").description("Tipo de registro para ingresos monetarios").build(),
                    RecordType.builder().name("Gasto").description("Tipo de registro para gastos monetarios").build(),
                    RecordType.builder().name("Transferencia").description("Tipo de registro para transferencias entre cuentas").build()
            );
            recordTypeRepository.saveAll(types);
        }
    }
}

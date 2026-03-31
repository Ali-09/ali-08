package com.example.ali_08.config;

import com.example.ali_08.model.PaymentMethod;
import com.example.ali_08.repository.PaymentMethodRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentMethodSeeder {

    private final PaymentMethodRepository paymentMethodRepository;

    @PostConstruct
    public void seed() {
        if (paymentMethodRepository.count() == 0) {
            List<PaymentMethod> methods = Arrays.asList(
                    PaymentMethod.builder().name("Efectivo").isActive(true).build(),
                    PaymentMethod.builder().name("Tarjeta").isActive(true).build(),
                    PaymentMethod.builder().name("Transferencia").isActive(true).build()
            );

            paymentMethodRepository.saveAll(methods);
        }
    }
}


package com.example.ali_08.config;

import com.example.ali_08.model.PaymentStatus;
import com.example.ali_08.repository.PaymentStatusRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentStatusSeeder {

    private final PaymentStatusRepository paymentStatusRepository;

    @PostConstruct
    public void seed() {
        if (paymentStatusRepository.count() == 0) {
            List<PaymentStatus> statuses = Arrays.asList(
                    PaymentStatus.builder().status("Pagado").color("green").build(),
                    PaymentStatus.builder().status("Pendiente").color("yellow").build(),
                    PaymentStatus.builder().status("Vencido").color("red").build()
            );

            paymentStatusRepository.saveAll(statuses);
        }
    }
}


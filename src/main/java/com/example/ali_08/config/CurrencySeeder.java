package com.example.ali_08.config;

import com.example.ali_08.model.Currency;
import com.example.ali_08.repository.CurrencyRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrencySeeder {

    private final CurrencyRepository currencyRepository;

    @PostConstruct
    public void seed() {

        if (currencyRepository.count() == 0) {

            currencyRepository.save(
                    new Currency(null, "MXN", "Peso mexicano", "$")
            );

            currencyRepository.save(
                    new Currency(null, "USD", "US Dollar", "$")
            );

            currencyRepository.save(
                    new Currency(null, "EUR", "Euro", "€")
            );
        }
    }
}

package com.example.ali_08.service;

import com.example.ali_08.dto.AccountDTO;
import com.example.ali_08.dto.DashboardResponse;
import com.example.ali_08.model.Account;
import com.example.ali_08.model.User;
import com.example.ali_08.repository.AccountRepository;
import com.example.ali_08.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public DashboardResponse getDashboardData() {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Account> userAccounts = accountRepository.findByUser(user);

        BigDecimal totalBalance = userAccounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<AccountDTO> accountDTOs = userAccounts.stream()
                .map(account -> AccountDTO.builder()
                        .id(account.getId())
                        .name(account.getName())
                        .balance(account.getBalance())
                        .currencyCode(account.getCurrency().getCode())
                        .type("Checking") // Default type
                        .build())
                .collect(Collectors.toList());

        return DashboardResponse.builder()
                .totalBalance(totalBalance)
                .currencyCode("USD") // Should be based on user's primary currency or first account
                .accounts(accountDTOs)
                .monthlyIncome(BigDecimal.ZERO) // Sum of income records for month (placeholder for now)
                .monthlyExpenses(BigDecimal.ZERO) // Sum of expense records for month (placeholder for now)
                .build();
    }
}

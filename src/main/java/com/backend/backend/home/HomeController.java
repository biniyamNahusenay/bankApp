package com.backend.backend.home;

import com.backend.backend.account.Account;
import com.backend.backend.account.AccountRepository;
import com.backend.backend.home.dto.HomeResponse;
import com.backend.backend.transaction.Transaction;
import com.backend.backend.transaction.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth") // Requires authentication
public class HomeController {

    private final AccountRepository accountRepository;
    private final TransactionService transactionService; // Inject TransactionService

    @GetMapping
    @Operation(summary = "Get user's home page details")
    public ResponseEntity<HomeResponse> getHomePageDetails() {
        String currentUserPhone = SecurityContextHolder.getContext().getAuthentication().getName();

        Account userAccount = accountRepository.findByPhone(currentUserPhone)
                .orElseThrow(() -> new RuntimeException("Account not found for user: " + currentUserPhone));

        List<Transaction> recentTransactions = transactionService.getRecentTransactions(currentUserPhone);

        HomeResponse response = HomeResponse.builder()
                .balance(userAccount.getBalance())
                .accountNumber(userAccount.getAccountNumber())
                .recentTransactions(recentTransactions)
                .build();

        return ResponseEntity.ok(response);
    }
}
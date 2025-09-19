package com.backend.backend.home.dto;

import com.backend.backend.transaction.Transaction;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HomeResponse {
    private String accountNumber;
    private BigDecimal balance;
    private List<Transaction> recentTransactions;
}

package com.backend.backend.transaction;

import com.backend.backend.account.Account;
import com.backend.backend.account.AccountRepository;
import com.backend.backend.auth.UserRepository;
import com.backend.backend.exception.InsufficientFundsException;
import com.backend.backend.exception.InvalidAmountException;
import com.backend.backend.transaction.dto.AirtimeTopupRequest;
import com.backend.backend.transaction.dto.InternalTransferRequest;
import com.backend.backend.transaction.dto.OtherBankTransferRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository; // To get current user's phone


    // Helper to get the current authenticated user's phone number
    private String getCurrentUserPhone() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // Helper for basic transaction creation
    private Transaction createBaseTransaction(String phone, TransactionType type, BigDecimal amount, TransactionStatus status) {
        return Transaction.builder()
                .phone(phone)
                .type(type)
                .amount(amount)
                .status(status)
                .build();
    }

    @Transactional
    public Transaction performInternalTransfer(InternalTransferRequest request) {
        // Validate amount
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Transfer amount must be positive.");
        }

        String sourcePhone = getCurrentUserPhone();
        Account sourceAccount = accountRepository.findByPhone(sourcePhone)
                .orElseThrow(() -> new RuntimeException("Source account not found for user: " + sourcePhone));

        if (sourceAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds.");
        }

        Account destinationAccount = accountRepository.findByAccountNumber(request.getDestinationAccountNumber())
                .orElseThrow(() -> new RuntimeException("Destination account not found: " + request.getDestinationAccountNumber()));

        // Debit source
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount()));
        accountRepository.save(sourceAccount);

        // Credit destination
        destinationAccount.setBalance(destinationAccount.getBalance().add(request.getAmount()));
        accountRepository.save(destinationAccount);

        Transaction transaction = createBaseTransaction(sourcePhone, TransactionType.INTERNAL, request.getAmount(), TransactionStatus.SUCCESS);
        transaction.setSourceAccountNumber(sourceAccount.getAccountNumber());
        transaction.setDestinationIdentifier(destinationAccount.getAccountNumber());

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction performOtherBankTransfer(OtherBankTransferRequest request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Transfer amount must be positive.");
        }

        String sourcePhone = getCurrentUserPhone();
        Account sourceAccount = accountRepository.findByPhone(sourcePhone)
                .orElseThrow(() -> new RuntimeException("Source account not found for user: " + sourcePhone));

        if (sourceAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds.");
        }

        // --- Simulate external bank integration ---
        // In a real application, this would involve calling an external payment gateway API.
        // For now, we'll just debit the user's account and mark the transaction as PENDING/SUCCESS based on simulation.
        // This is a simplified simulation!

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount()));
        accountRepository.save(sourceAccount);

        Transaction transaction = createBaseTransaction(sourcePhone, TransactionType.OTHER_BANK, request.getAmount(), TransactionStatus.SUCCESS); // Assume success for simulation
        transaction.setSourceAccountNumber(sourceAccount.getAccountNumber());
        transaction.setDestinationIdentifier(request.getDestinationAccountNumber()); // Store destination account number
        // Optionally, store destination bank name, recipient name in a transaction details field if needed

        log.info("Simulating transfer to external bank: {} for account {} with amount {}",
                request.getDestinationBankName(), request.getDestinationAccountNumber(), request.getAmount());

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction performAirtimeTopup(AirtimeTopupRequest request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Transfer amount must be positive.");
        }

        String sourcePhone = getCurrentUserPhone();
        Account sourceAccount = accountRepository.findByPhone(sourcePhone)
                .orElseThrow(() -> new RuntimeException("Source account not found for user: " + sourcePhone));

        if (sourceAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds.");
        }

        // --- Simulate airtime top-up integration ---
        // In a real application, this would involve calling an airtime top-up API (e.g., a telecom provider's API).
        // For now, we'll just debit the user's account and mark the transaction as PENDING/SUCCESS.

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount()));
        accountRepository.save(sourceAccount);

        Transaction transaction = createBaseTransaction(sourcePhone, TransactionType.TOPUP, request.getAmount(), TransactionStatus.SUCCESS); // Assume success for simulation
        transaction.setSourceAccountNumber(sourceAccount.getAccountNumber()); // Source is the user's account
        transaction.setDestinationIdentifier(request.getRecipientPhoneNumber()); // Destination is the phone receiving airtime

        log.info("Simulating airtime top-up for {} with amount {}",
                request.getRecipientPhoneNumber(), request.getAmount());

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getRecentTransactions(String phone) {
        return transactionRepository.findTop5ByPhoneOrderByCreatedDateDesc(phone);
    }

    public List<Transaction> getTransactionHistory(String phone) {
        return transactionRepository.findByPhoneOrderByCreatedDateDesc(phone);
    }

    public Page<Transaction> getTransactionsPage(int page, int size) {
        String phone = getCurrentUserPhone();
        Pageable pageable = PageRequest.of(page, size);
        return transactionRepository.findByPhoneOrderByCreatedDateDesc(phone, pageable);
    }
}
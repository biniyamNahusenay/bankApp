package com.backend.backend;

import com.backend.backend.account.AccountRepository;
import com.backend.backend.auth.UserRepository;
import com.backend.backend.transaction.TransactionRepository;
import com.backend.backend.account.Account;
import com.backend.backend.transaction.Transaction;
import com.backend.backend.transaction.TransactionStatus;
import com.backend.backend.transaction.TransactionType;
import com.backend.backend.auth.User;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataInitializer {
    private final AccountRepository accountRepo;
    private final TransactionRepository txRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder; // Inject password encoder

    @PostConstruct
    public void init(){
        if(userRepo.count() == 0){ // Check if any users exist
            // Create a test user
            String testPhone = "+251911000111";
            String testPin = "1234"; // Raw PIN
            String hashedPin = passwordEncoder.encode(testPin); // Hash the PIN

            User user = User.builder()
                    .id(UUID.randomUUID().toString()) // Generate UUID for the user
                    .phone(testPhone)
                    .pin(hashedPin)
                    .active(true) // Mark as enabled directly for testing
                    .build();
            userRepo.save(user);

            // Create account for the test user
            Account acc = Account.builder()
                    .accountNumber("101200300")
                    .phone(testPhone)
                    .balance(BigDecimal.valueOf(1500))
                    .build();
            accountRepo.save(acc);

            // Create another account for internal transfer destination
            User user2 = User.builder()
                    .id(UUID.randomUUID().toString())
                    .phone("+251911222333")
                    .pin(passwordEncoder.encode("5678"))
                    .active(true)
                    .build();
            userRepo.save(user2);

            Account acc2 = Account.builder()
                    .accountNumber("101200301")
                    .phone("+251911222333")
                    .balance(BigDecimal.valueOf(500))
                    .build();
            accountRepo.save(acc2);


            // Create sample transactions for the first user
            List<Transaction> txs = List.of(
                    Transaction.builder()
                            .phone(acc.getPhone())
                            .type(TransactionType.TOPUP)
                            .amount(BigDecimal.valueOf(100))
                            .status(TransactionStatus.SUCCESS)
                            .createdDate(LocalDateTime.now().minusDays(1))
                            .build(),
                    Transaction.builder()
                            .phone(acc.getPhone())
                            .type(TransactionType.INTERNAL)
                            .amount(BigDecimal.valueOf(250))
                            .status(TransactionStatus.SUCCESS)
                            .sourceAccountNumber(acc.getAccountNumber())
                            .destinationIdentifier(acc2.getAccountNumber())
                            .createdDate(LocalDateTime.now().minusHours(6))
                            .build(),
                    Transaction.builder()
                            .phone(acc.getPhone())
                            .type(TransactionType.OTHER_BANK)
                            .amount(BigDecimal.valueOf(500))
                            .status(TransactionStatus.SUCCESS)
                            .sourceAccountNumber(acc.getAccountNumber())
                            .destinationIdentifier("987654321")
                            .createdDate(LocalDateTime.now().minusMinutes(30))
                            .build()
            );
            txRepo.saveAll(txs);
        }
    }
}
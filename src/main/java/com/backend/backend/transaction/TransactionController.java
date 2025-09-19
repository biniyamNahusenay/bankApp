package com.backend.backend.transaction;

import com.backend.backend.transaction.dto.AirtimeTopupRequest;
import com.backend.backend.transaction.dto.InternalTransferRequest;
import com.backend.backend.transaction.dto.OtherBankTransferRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth") // Indicates that this controller requires JWT authentication
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/internal")
    @Operation(summary = "Perform an internal bank transfer")
    public ResponseEntity<Transaction> internalTransfer(@Valid @RequestBody InternalTransferRequest request) {
        Transaction transaction = transactionService.performInternalTransfer(request);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @PostMapping("/other-bank")
    @Operation(summary = "Perform a transfer to another bank")
    public ResponseEntity<Transaction> otherBankTransfer(@Valid @RequestBody OtherBankTransferRequest request) {
        Transaction transaction = transactionService.performOtherBankTransfer(request);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @PostMapping("/airtime")
    @Operation(summary = "Perform an airtime top-up")
    public ResponseEntity<Transaction> airtimeTopup(@Valid @RequestBody AirtimeTopupRequest request) {
        Transaction transaction = transactionService.performAirtimeTopup(request);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get paginated transaction history for current user")
    public ResponseEntity<Page<Transaction>> getTransactions(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        Page<Transaction> transactions = transactionService.getTransactionsPage(page, size);
        return ResponseEntity.ok(transactions);
    }

    // You can add more endpoints here for transaction history and filtering later if needed
    // GET /api/transactions/history
}
package com.backend.backend.transaction.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OtherBankTransferRequest {
    @NotBlank(message = "Destination bank name is required")
    private String destinationBankName; // Could be an enum or external lookup
    @NotBlank(message = "Destination account number is required")
    private String destinationAccountNumber;
    @NotBlank(message = "Recipient name is required")
    private String recipientName; // For external transfers for verification

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
}

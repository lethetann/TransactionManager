package com.example.fintech.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record Transaction(UUID id,
        @NotBlank(message = "Type must be INCOME or EXPENSE") @Pattern(regexp = "^(INCOME|EXPENSE)$", flags = Pattern.Flag.CASE_INSENSITIVE) String type,

        @Positive(message = "Amount must be positive") @Max(value = 1000000, message = "Amount cannot exceed 1,000,000") double amount,

        @NotBlank(message = "Description is required") @Size(min = 3, max = 255, message = "Description must be 3-255 characters") String description,

        @PastOrPresent(message = "Timestamp cannot be in the future") LocalDateTime timestamp) {
    public Transaction {
        if (id == null)
            id = UUID.randomUUID();
        if (timestamp == null)
            timestamp = LocalDateTime.now();
    }
}

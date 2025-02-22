package com.example.fintech.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.*;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class TransactionTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidationForValidTransaction() {
        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                "INCOME",
                100.0,
                "Salary",
                LocalDateTime.now());
        assertEquals(0, validator.validate(transaction).size());
    }

    @Test
    void shouldFailValidationForInvalidType() {
        Transaction transaction = new Transaction(
                null,
                "INVALID",
                -100.0,
                "A",
                LocalDateTime.now().plusHours(1));
        assertEquals(4, validator.validate(transaction).size());
    }

    @Test
    void shouldValidateAmountConstraints() {
        Transaction t1 = new Transaction(null, "INCOME", 0.01, "Minimum", null);
        assertEquals(0, validator.validate(t1).size());

        Transaction t2 = new Transaction(null, "INCOME", 1000000.0, "Maximum", null);
        assertEquals(0, validator.validate(t2).size());

        Transaction t3 = new Transaction(null, "INCOME", 1000000.01, "Overflow", null);
        assertEquals(1, validator.validate(t3).size());
    }

    @Test
    void shouldValidateDescriptionConstraints() {
        String minDesc = "A".repeat(3);
        String maxDesc = "A".repeat(255);
        String invalidDesc = "A".repeat(256);

        Transaction t1 = new Transaction(null, "INCOME", 100.0, minDesc, null);
        Transaction t2 = new Transaction(null, "INCOME", 100.0, maxDesc, null);
        Transaction t3 = new Transaction(null, "INCOME", 100.0, invalidDesc, null);

        assertEquals(0, validator.validate(t1).size());
        assertEquals(0, validator.validate(t2).size());
        assertEquals(1, validator.validate(t3).size());
    }

    @Test
    void shouldAutoGenerateIdAndTimestamp() {
        Transaction t = new Transaction(null, "INCOME", 100.0, "Auto Generate", null);
        assertNotNull(t.id());
        assertNotNull(t.timestamp());
    }
}

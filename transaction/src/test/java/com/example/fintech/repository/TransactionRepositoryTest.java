package com.example.fintech.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.fintech.exception.DuplicateTransactionException;
import com.example.fintech.model.Transaction;

public class TransactionRepositoryTest {
    private TransactionRepository repository;
    private UUID testId;

    @BeforeEach
    void setUp() {
        repository = new TransactionRepository();
        testId = UUID.randomUUID();
        repository.save(new Transaction(testId, "INCOME", 100.0, "Test", LocalDateTime.now()));
    }

    @Test
    void shouldSaveAndRetrieveTransaction() {
        Optional<Transaction> found = repository.findById(testId);
        assertTrue(found.isPresent());
        assertEquals(100.0, found.get().amount());
    }

    @Test
    void shouldDeleteTransaction() {
        repository.deleteById(testId);
        assertFalse(repository.findById(testId).isPresent());
    }

    @Test
    void shouldHandleEmptyRepository() {
        TransactionRepository emptyRepo = new TransactionRepository();
        assertFalse(emptyRepo.findById(UUID.randomUUID()).isPresent());
        assertEquals(0, emptyRepo.findAll().size());
    }

    @Test
    void shouldUpdateExistingTransaction() {
        UUID id = UUID.randomUUID();
        Transaction original = new Transaction(id, "INCOME", 100.0, "Original", null);
        Transaction updated = new Transaction(id, "EXPENSE", 200.0, "Updated", null);

        repository.save(original);
        repository.save(updated);

        Optional<Transaction> result = repository.findById(id);
        assertTrue(result.isPresent());
        assertEquals("EXPENSE", result.get().type());
        assertEquals(200.0, result.get().amount());
    }

    @Test
    void shouldNotFindNonExistentId() {
        assertFalse(repository.findById(UUID.randomUUID()).isPresent());
    }

    @Test
    void shouldHandleMultipleSaves() {
        Transaction t = new Transaction(null, "INCOME", 100.0, "Test", null);
        assertThrows(DuplicateTransactionException.class, () -> repository.save(t));
        assertEquals(1, repository.findAll().size());
    }
}

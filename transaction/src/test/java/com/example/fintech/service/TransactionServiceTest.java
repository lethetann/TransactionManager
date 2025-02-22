package com.example.fintech.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.fintech.exception.DuplicateTransactionException;
import com.example.fintech.exception.TransactionNotFoundException;
import com.example.fintech.model.Page;
import com.example.fintech.model.Transaction;
import jakarta.validation.ConstraintViolationException;

@SpringBootTest
public class TransactionServiceTest {

    @Autowired
    private TransactionService service;

    @BeforeEach
    void setUp(){
        service.deleteAllTransactions();
    }

    @Test
    void shouldCreateTransaction() {
        Transaction transaction = new Transaction(
                null, "INCOME", 200.0, "Salary", null);
        Transaction created = service.createTransaction(transaction);
        assertNotNull(created.id());
    }

    @Test
    void shouldRejectDuplicateTransaction() {
        Transaction t1 = new Transaction(null, "EXPENSE", 50.0, "Lunch", null);
        service.createTransaction(t1);
        assertThrows(DuplicateTransactionException.class, () -> service.createTransaction(t1));
    }

    @Test
    void shouldPaginateTransactions() {
        for (int i = 0; i < 15; i++) {
            service.createTransaction(new Transaction(null, "INCOME", 100 + i, "Test", null));
        }
        Page<Transaction> page = service.getTransactions(1, 5);
        assertEquals(5, page.getContent().size());
        assertEquals(1, page.getCurrentPage());
        assertEquals(3, page.getTotalPages());
    }

    @Test
    void shouldUpdateTransactionSuccessfully() {
        Transaction original = service.createTransaction(new Transaction(null, "INCOME", 100.0, "Original", null));
        Transaction updated = service.updateTransaction(original.id(),
                new Transaction(null, "EXPENSE", 200.0, "Updated", null));

        assertEquals(original.id(), updated.id());
        assertEquals("EXPENSE", updated.type());
        assertEquals(200.0, updated.amount());
    }

    @Test
    void shouldThrowWhenUpdatingNonExistentTransaction() {
        UUID fakeId = UUID.randomUUID();
        assertThrows(TransactionNotFoundException.class,
                () -> service.updateTransaction(fakeId, new Transaction(null, "EXPENSE", 50.0, "Test", null)));
    }

    @Test
    void shouldHandleEmptyPagination() {
        Page<Transaction> page = service.getTransactions(0, 10);
        assertEquals(0, page.getContent().size());
        assertEquals(0, page.getTotalElements());
    }

    @Test
    void shouldThrowWhenPageIsNegative() {
        assertThrows(ConstraintViolationException.class, () -> service.getTransactions(-1, 10));
    }

    @Test
    void shouldThrowWhenSizeIsOver100() {
        assertThrows(ConstraintViolationException.class, () -> service.getTransactions(0, 101));
    }

}

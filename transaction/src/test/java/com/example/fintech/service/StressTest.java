package com.example.fintech.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.fintech.exception.DuplicateTransactionException;
import com.example.fintech.model.Transaction;

@SpringBootTest
public class StressTest {
    @Autowired
    private TransactionService service;

    @BeforeEach
    void setUp() {
        service.deleteAllTransactions();
    }

    @Test
    void shouldHandleConcurrentCreations() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                double randomAmount = 100.0 + ThreadLocalRandom.current().nextDouble(0, 1000);
                service.createTransaction(new Transaction(
                        null, "INCOME", randomAmount, "Stress Test " + randomAmount, null));
                latch.countDown();
            });
        }

        latch.await();
        assertEquals(threadCount, service.getAllTransactionsWithoutPaging().size());
    }

    @Test
    void shouldHandleConcurrentReadsAndWrites() throws InterruptedException {
        int writers = 50;
        int readers = 50;
        ExecutorService executor = Executors.newFixedThreadPool(writers + readers);
        CountDownLatch latch = new CountDownLatch(writers + readers);

        for (int i = 0; i < writers; i++) {
            executor.execute(() -> {
                double randomAmount = 10.0 + ThreadLocalRandom.current().nextDouble(0, 1000);
                service.createTransaction(new Transaction(
                        null, "EXPENSE", randomAmount, "Concurrency Test " + randomAmount, null));
                latch.countDown();
            });
        }

        for (int i = 0; i < readers; i++) {
            executor.execute(() -> {
                service.getTransactions(0, 10);
                latch.countDown();
            });
        }

        latch.await();

        assertEquals(writers, service.getAllTransactionsWithoutPaging().size());
    }

    @Test
    void shouldHandleMixedReadWriteOperations() throws InterruptedException {
        int totalOperations = 200;
        ExecutorService executor = Executors.newFixedThreadPool(50);
        CountDownLatch latch = new CountDownLatch(totalOperations);

        for (int i = 0; i < totalOperations; i++) {
            if (i % 2 == 0) {
                executor.execute(() -> {
                    double randomAmount = 10.0 + ThreadLocalRandom.current().nextDouble(0, 100);
                    service.createTransaction(
                            new Transaction(null, "INCOME", randomAmount, "Stress Test " + randomAmount, null));
                    latch.countDown();
                });
            } else {
                executor.execute(() -> {
                    service.getTransactions(0, 10);
                    latch.countDown();
                });
            }
        }

        latch.await();

        assertEquals(totalOperations / 2,
                service.getAllTransactionsWithoutPaging().size());
    }

    @Test
    void shouldPreventDuplicateUnderHighConcurrency() throws InterruptedException {
        Transaction template = new Transaction(null, "EXPENSE", 50.0, "Dupe Test",
                null);
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                try {
                    service.createTransaction(template);
                    successCount.incrementAndGet();
                } catch (DuplicateTransactionException e) {
                    failureCount.incrementAndGet();
                }
                latch.countDown();
            });
        }

        latch.await();

        assertEquals(1, successCount.get());
        assertEquals(threadCount - 1, failureCount.get());
    }

}

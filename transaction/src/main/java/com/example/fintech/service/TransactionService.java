package com.example.fintech.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.example.fintech.exception.TransactionNotFoundException;
import com.example.fintech.model.Page;
import com.example.fintech.model.Pageable;
import com.example.fintech.model.Transaction;
import com.example.fintech.repository.TransactionRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Service
@Validated
public class TransactionService {

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    @CacheEvict(value = "transactions", allEntries = true)
    public Transaction createTransaction(@Valid Transaction transaction) {
        return repository.save(transaction);
    }

    @Cacheable("transactions")
    public Page<Transaction> getTransactions(
            @Min(value = 0, message = "Page must not less than zero") int page,
            @Min(value = 1, message = "Page size mut not less than one") @Max(value = 100, message = "Page size cannot exceed 100") int size) {
        Pageable pageable = new Pageable(page, size);
        List<Transaction> allTransactions = repository.findAll();
        return applyPaging(allTransactions, pageable);
    }

    public List<Transaction> getAllTransactionsWithoutPaging() {
        return repository.findAll();
    }

    private Page<Transaction> applyPaging(List<Transaction> data, Pageable pageable) {
        int total = data.size();
        int fromIndex = pageable.getOffset();
        int toIndex = Math.min(fromIndex + pageable.getSize(), total);

        if (fromIndex >= total) {
            return new Page<>(Collections.emptyList(), total, pageable);
        }

        List<Transaction> pageContent = data.subList(fromIndex, toIndex);
        return new Page<Transaction>(pageContent, total, pageable);
    }

    @CacheEvict(value = "transactions", allEntries = true)
    public void deleteTransaction(UUID id) {
        if (!repository.findById(id).isPresent()) {
            throw new TransactionNotFoundException("Transaction not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @CacheEvict(value = "transactions", allEntries = true)
    public Transaction updateTransaction(UUID id, @Valid Transaction transaction) {
        return repository.findById(id)
                .map(old -> repository.save(new Transaction(
                        id,
                        transaction.type(),
                        transaction.amount(),
                        transaction.description(),
                        LocalDateTime.now())))
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + id));
    }

    @CacheEvict(value = "transactions", allEntries = true)
    public void deleteAllTransactions() {
        repository.deleteAll();
    }
}

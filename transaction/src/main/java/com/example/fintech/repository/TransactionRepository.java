package com.example.fintech.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.example.fintech.exception.DuplicateTransactionException;
import com.example.fintech.model.Transaction;

@Repository
public class TransactionRepository {

    private final ConcurrentHashMap<UUID, Transaction> idStore = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, UUID> businessKeyStore = new ConcurrentHashMap<>();

    private String generateBusinessKey(Transaction t) {
        return String.format("%s|%.2f|%s",
                t.type().toLowerCase(),
                t.amount(),
                t.description().toLowerCase());
    }

    public Transaction save(Transaction transaction) {
        String businessKey = generateBusinessKey(transaction);
        if (businessKeyStore.putIfAbsent(businessKey, transaction.id()) != null) {
            throw new DuplicateTransactionException("Duplicate transaction");
        }
        idStore.put(transaction.id(), transaction);
        return transaction;
    }

    public List<Transaction> findAll() {
        return new ArrayList<>(idStore.values());
    }

    public Optional<Transaction> findById(UUID id) {
        return Optional.ofNullable(idStore.get(id));
    }

    public void deleteById(UUID id) {
        Transaction removed = idStore.remove(id);
        if (removed != null) {
            businessKeyStore.remove(generateBusinessKey(removed));
        }
    }

    public void deleteAll() {
        idStore.clear();
        businessKeyStore.clear();
    }
}

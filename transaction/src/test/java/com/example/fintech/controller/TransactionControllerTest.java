package com.example.fintech.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.fintech.model.Transaction;
import com.example.fintech.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;
import java.util.stream.IntStream;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionService service;

    @Test
    void shouldCreateTransactionViaAPI() throws Exception {
        Transaction transaction = new Transaction(null, "INCOME", 300.0, "API Test", null);
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void shouldReturnBadRequestForInvalidTransaction() throws Exception {
        Transaction invalid = new Transaction(null, "INVALID", -100.0, "A", null);
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteTransaction() throws Exception {
        Transaction t = service.createTransaction(new Transaction(null, "EXPENSE", 50.0, "Delete Test", null));
        mockMvc.perform(delete("/api/transactions/" + t.id()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnPaginatedResults() throws Exception {
        IntStream.range(0, 10).forEach(i -> 
            service.createTransaction(new Transaction(null, "INCOME", 100 + i, "Test " + i, null))
        );

        mockMvc.perform(get("/api/transactions?page=1&size=5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(5))
            .andExpect(jsonPath("$.currentPage").value(1))
            .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    void shouldReturnEmptyPageForOutOfRange() throws Exception {
        mockMvc.perform(get("/api/transactions?page=100&size=10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void shouldHandleInvalidUpdateRequest() throws Exception {
        UUID fakeId = UUID.randomUUID();
        Transaction invalidUpdate = new Transaction(null, "INVALID", -100.0, "", null);

        mockMvc.perform(put("/api/transactions/" + fakeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404ForNonExistentDelete() throws Exception {
        UUID fakeId = UUID.randomUUID();
        mockMvc.perform(delete("/api/transactions/" + fakeId))
                .andExpect(status().isNotFound());
    }
}

package com.example.fintech.model;

import java.util.List;

import lombok.Getter;

@Getter
public class Page<T> {
    private List<T> content;
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private int size;

    public Page(List<T> content, long totalElements, Pageable pageable) {
        this.content = content;
        this.totalElements = totalElements;
        this.currentPage = pageable.getPage();
        this.size = pageable.getSize();
        this.totalPages = (int) Math.ceil((double) totalElements / size);
    }
}

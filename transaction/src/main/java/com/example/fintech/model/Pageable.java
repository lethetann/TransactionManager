package com.example.fintech.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Pageable {
    private int page;
    private int size;

    public int getOffset() {
        return page * size;
    }

}

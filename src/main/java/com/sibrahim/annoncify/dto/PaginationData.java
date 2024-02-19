package com.sibrahim.annoncify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationData {

    private long total;
    private int count;
    private int perPage;
    private int currentPage;
    private int totalPages;

    public PaginationData(Page<?> page) {
        this.total = page.getTotalElements();
        this.count = page.getNumberOfElements();
        this.perPage = page.getSize();
        this.currentPage = page.getNumber() + 1; // Page is zero-based. Add one since it usually starts from 1.
        this.totalPages = page.getTotalPages();
    }

}

package com.socialmediaapp.Util;

import lombok.Getter;

import java.util.List;
@Getter
public class Page<T> {
    private final List<T> content;
    private final int pageNumber;
    private final int pageSize;
    private final int totalElements;
    private final int totalPages;

    public Page(List<T> content, int pageNumber, int pageSize, int totalElements) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / pageSize);
    }

}

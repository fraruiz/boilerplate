package com.example.shared.domain.criteria;

public record Page(Integer number, Integer size) {
    private final static Integer ZERO = 0;
    private final static Integer TEN = 10;

    public static Page initial() {
        return new Page(ZERO, TEN);
    }

    public static Page from(Integer number, Integer size) {
        return new Page(number, size);
    }

    public static Page next(Page page) {
        return new Page(page.number() + 1, page.size());
    }
}

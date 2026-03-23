package com.example.shared.domain.criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Criteria {
    private final Optional<Page> page;

    public Criteria() {
        this.page = Optional.empty();
    }

    public Criteria(Page page) {
        this.page = Optional.of(page);
    }

    public Optional<Page> page() {
        return page;
    }

    public List<Object> primitives() {
        List<Object> args = new ArrayList<>();

        page.ifPresent(it -> {
            args.add(it.size());
            args.add(it.number() * it.size());
        });

        return args;
    }

    public abstract Object[] toPrimitives();
}

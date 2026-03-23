package com.example.shared.domain.bus.query;

public interface QueryHandler<Q extends Query, R extends Response> {
    R execute(Q query);

    Class<Q> query();
}

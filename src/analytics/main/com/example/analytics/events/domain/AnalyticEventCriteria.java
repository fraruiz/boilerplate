package com.example.analytics.events.domain;

import com.example.shared.domain.criteria.Criteria;
import com.example.shared.domain.criteria.Page;
import com.example.shared.domain.valueobjects.Identifier;

import java.util.ArrayList;
import java.util.List;

public final class AnalyticEventCriteria extends Criteria {
    private final Identifier aggregateId;

    public AnalyticEventCriteria(Page page, Identifier aggregateId) {
        super(page);
        this.aggregateId = aggregateId;
    }

    public AnalyticEventCriteria(Identifier aggregateId) {
        super();
        this.aggregateId = aggregateId;
    }

    public Identifier aggregateId() {
        return aggregateId;
    }

    @Override
    public Object[] toPrimitives() {
        List<Object> args = new ArrayList<>();

        args.add(aggregateId.value());
        args.addAll(super.primitives());

        return args.toArray();
    }
}

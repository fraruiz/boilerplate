package com.example.analytics.shared.infrastructure.persistence;

import com.example.shared.infrastructure.persistence.DelegatedDataSource;

import javax.sql.DataSource;

public final class AnalyticsDataSource extends DelegatedDataSource {
    public AnalyticsDataSource(DataSource delegate) {
        super(delegate);
    }
}

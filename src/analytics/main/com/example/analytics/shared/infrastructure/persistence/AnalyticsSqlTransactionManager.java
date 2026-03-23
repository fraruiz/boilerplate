package com.example.analytics.shared.infrastructure.persistence;

import com.example.shared.infrastructure.persistence.SqlTransactionManager;

import javax.sql.DataSource;

public class AnalyticsSqlTransactionManager extends SqlTransactionManager {
    public AnalyticsSqlTransactionManager(DataSource dataSource) {
        super(dataSource);
    }
}

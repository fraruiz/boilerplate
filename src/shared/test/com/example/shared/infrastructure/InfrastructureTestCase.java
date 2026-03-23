package com.example.shared.infrastructure;

import com.example.shared.domain.date.DateProvider;
import com.example.shared.domain.logs.Logger;
import com.example.shared.domain.mappers.Mapper;
import com.example.shared.infrastructure.date.DefaultDateProvider;
import com.example.shared.infrastructure.logs.Slf4jLogger;
import com.example.shared.infrastructure.mapper.JsonMapper;
import com.example.shared.infrastructure.persistence.DatabaseHelper;
import org.apache.commons.dbutils.ResultSetHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import javax.sql.DataSource;

public abstract class InfrastructureTestCase {
    protected Mapper mapper;
    protected Logger logger;
    protected DateProvider dateProvider;
    protected DataSource dataSource;

    @BeforeEach
    protected void setUp() {
        this.mapper = new JsonMapper();
        this.logger = new Slf4jLogger();
        this.dateProvider = new DefaultDateProvider();

        this.dataSource = DatabaseHelper.dataSource();

        DatabaseHelper.dump(this.dataSource);
    }

    @AfterEach
    protected void tearDown() {
        DatabaseHelper.drop(this.dataSource);
    }

    protected void insert(String sql, Object... args) {
        DatabaseHelper.insert(dataSource, sql, args);
    }

    protected <T> T query(String sql, ResultSetHandler<T> handler, Object... args) {
        return DatabaseHelper.query(dataSource, sql, handler, args);
    }
}

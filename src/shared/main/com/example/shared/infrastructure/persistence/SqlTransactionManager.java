package com.example.shared.infrastructure.persistence;

import com.example.shared.domain.transactions.TransactionManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class SqlTransactionManager implements TransactionManager {
    private final DataSource dataSource;

    public SqlTransactionManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void execute(Runnable runnable) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            TransactionContext.set(connection);
            try {
                runnable.run();
                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw new InternalError(e.getMessage());
            } finally {
                TransactionContext.clear();
            }
        } catch (SQLException e) {
            throw new InternalError(e.getMessage());
        }
    }
}

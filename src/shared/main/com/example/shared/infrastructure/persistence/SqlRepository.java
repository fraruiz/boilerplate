package com.example.shared.infrastructure.persistence;

import com.example.shared.domain.logs.Logger;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

public abstract class SqlRepository {
    private final Logger logger;
    private final QueryRunner runner;

    protected SqlRepository(Logger logger, DataSource dataSource) {
        this.logger = logger;
        this.runner = new QueryRunner(dataSource);
    }

    protected void runInsert(String command, Object... args) {
        logger.info("SQL Insert", context(command, args));
        try {
            if (TransactionContext.isActive()) {
                runner.update(TransactionContext.get(), command, args);
            } else {
                runner.update(command, args);
            }
        } catch (SQLException e) {
            logger.critical("SQL Insert failed", context(command, args));
            throw new InternalError(e.getMessage());
        }
    }

    protected void runUpdate(String command, Object... args) {
        logger.info("SQL Update", context(command, args));
        try {
            if (TransactionContext.isActive()) {
                runner.update(TransactionContext.get(), command, args);
            } else {
                runner.update(command, args);
            }
        } catch (SQLException e) {
            logger.critical("SQL Update failed", context(command, args));
            throw new InternalError(e.getMessage());
        }
    }

    protected void runDelete(String command, Object... args) {
        logger.info("SQL Delete", context(command, args));
        try {
            if (TransactionContext.isActive()) {
                runner.update(TransactionContext.get(), command, args);
            } else {
                runner.update(command, args);
            }
        } catch (SQLException e) {
            logger.critical("SQL Delete failed", context(command, args));
            throw new InternalError(e.getMessage());
        }
    }

    protected <T> T runQuery(String command, ResultSetHandler<T> handler, Object... args) throws SQLException {
        logger.info("SQL Query", context(command, args));
        try {
            if (TransactionContext.isActive()) {
                return runner.query(TransactionContext.get(), command, handler, args);
            } else {
                return runner.query(command, handler, args);
            }
        } catch (SQLException e) {
            logger.critical("SQL Query failed", context(command, args));
            throw new InternalError(e.getMessage());
        }
    }

    private Map<String, Serializable> context(String command, Object... args) {
        return Map.of(
            "command", command,
            "args", Arrays.toString(args)
        );
    }
}

package com.example.shared.infrastructure.persistence;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Enumeration;

public final class DatabaseHelper {

    private static final String DUMP_FILE = "database/dump.sql";
    private static final String DROP_FILE = "database/drop.sql";

    private DatabaseHelper() {
    }

    public static void dump(DataSource dataSource) {
        executeAll(dataSource, DUMP_FILE);
    }

    public static void drop(DataSource dataSource) {
        executeAll(dataSource, DROP_FILE);
    }

    public static HikariDataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl("jdbc:h2:mem:db;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_DELAY=-1;NON_KEYWORDS=VALUE,KEY,CONFLICT");
        config.setUsername("sa");
        config.setPassword("");
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        return new HikariDataSource(config);
    }

    public static void insert(DataSource dataSource, String sql, Object... args) {
        try {
            new QueryRunner(dataSource).update(sql, args);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T query(DataSource dataSource, String sql, ResultSetHandler<T> handler, Object... args) {
        try {
            return new QueryRunner(dataSource).query(sql, handler, args);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void executeAll(DataSource dataSource, String resourceName) {
        try {
            Enumeration<URL> resources = DatabaseHelper.class.getClassLoader().getResources(resourceName);
            for (URL url : Collections.list(resources)) {
                try (InputStream is = url.openStream()) {
                    String sql = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    execute(dataSource, sql);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void execute(DataSource dataSource, String sql) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

package com.example.shared.infrastructure.persistence;

import com.example.shared.domain.env.Environment;
import com.example.shared.domain.logs.Logger;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.Locale;
import java.util.Map;

public final class DataSourceProvider {
    private final static String H2 = "H2";
    private final static String POSTGRESQL = "POSTGRESQL";
    private final static String MYSQL = "MYSQL";

    private final static Map<String, String> DBMS_DRIVERS = Map.of(
            H2, "org.h2.Driver",
            POSTGRESQL, "org.postgresql.Driver",
            MYSQL, "com.mysql.cj.jdbc.Driver");


    private final Logger logger;
    private final Environment environment;


    public DataSourceProvider(Logger logger, Environment environment) {
        this.logger = logger;
        this.environment = environment;
    }

    public DataSource execute(String db,
                              String host,
                              Integer port,
                              String dbms,
                              String username,
                              String password,
                              Integer maxPoolSize,
                              Integer idlePoolSize,
                              Integer idleTimeout,
                              Integer connectionTimeout,
                              Boolean autoCommit) {
        logger.info(String.format("Initializing %s data source", db));

        String driver = DBMS_DRIVERS.get(dbms.toUpperCase(Locale.ROOT));

        HikariConfig config = new HikariConfig();

        config.setDriverClassName(driver);
        config.setJdbcUrl(getJdbcUrl(db, host, port, dbms));
        config.setUsername(environment.execute(username, username));
        config.setPassword(environment.execute(password, password));
        config.setMinimumIdle(idlePoolSize);
        config.setMaximumPoolSize(maxPoolSize);
        config.setConnectionTimeout(connectionTimeout);
        config.setIdleTimeout(idleTimeout);
        config.setAutoCommit(autoCommit);

        if (!H2.equals(dbms.toLowerCase(Locale.ROOT))) {
            config.addDataSourceProperty("serverTimezone", "UTC");
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("useLocalTransactionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            config.addDataSourceProperty("cacheServerConfiguration", "true");
            config.addDataSourceProperty("elideSetAutoCommits", "true");
            config.addDataSourceProperty("maintainTimeStats", "false");
        }

        return new HikariDataSource(config);
    }

    private String getJdbcUrl(String db, String host, Integer port, String dbms) {
        return switch (dbms) {
            case H2 -> String.format("jdbc:h2:mem:%s;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH", db);
            case POSTGRESQL -> String.format("jdbc:postgresql://%s:%d/%s", host, port, db);
            case MYSQL -> String.format("jdbc:mysql://%s:%d/%s?useSSL=false", host, port, db);
            default -> throw new InternalError("can not factory jdbc url");
        };
    }
}

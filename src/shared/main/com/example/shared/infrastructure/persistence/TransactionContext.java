package com.example.shared.infrastructure.persistence;

import java.sql.Connection;

public final class TransactionContext {
    private static final ThreadLocal<Connection> CURRENT = new ThreadLocal<>();

    private TransactionContext() {}

    public static void set(Connection connection) {
        CURRENT.set(connection);
    }

    public static Connection get() {
        return CURRENT.get();
    }

    public static boolean isActive() {
        return CURRENT.get() != null;
    }

    public static void clear() {
        CURRENT.remove();
    }
}

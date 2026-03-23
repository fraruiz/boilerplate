package com.example.shared.domain.transactions;

public interface TransactionManager {
    void execute(Runnable runnable);
}

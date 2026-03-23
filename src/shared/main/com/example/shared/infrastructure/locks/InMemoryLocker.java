package com.example.shared.infrastructure.locks;

import com.example.shared.domain.errors.client.Locked;
import com.example.shared.domain.locks.Locker;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class InMemoryLocker implements Locker {
    private final ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    @Override
    public void execute(Serializable context, Serializable resource, Serializable key, Runnable runnable) {
        String lockKey = String.format("%s:%s:%S", context, resource, key);
        ReentrantLock lock = locks.computeIfAbsent(lockKey, _ -> new ReentrantLock());

        if (!lock.tryLock()) {
            throw new Locked(lockKey + " is already locked");
        }

        try {
            runnable.run();
        } finally {
            lock.unlock();
        }
    }
}

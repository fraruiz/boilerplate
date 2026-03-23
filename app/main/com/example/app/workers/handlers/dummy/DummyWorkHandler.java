package com.example.app.workers.handlers.dummy;

import com.example.app.workers.handlers.WorkHandler;

import java.time.Duration;

public class DummyWorkHandler extends WorkHandler {
    @Override
    public void execute() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warning("Thread was interrupted during sleep");
        }
    }

    @Override
    public Duration period() {
        return Duration.ofSeconds(30);
    }
}

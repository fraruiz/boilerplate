package com.example.shared.domain.bus.command;

public final class CommandHandlerExecutionError extends InternalError {
    public CommandHandlerExecutionError(Throwable cause) {
        super(cause);
    }
}

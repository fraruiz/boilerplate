package com.example.shared.domain.bus.command;

public interface CommandBus {
    void execute(Command command) throws CommandHandlerExecutionError;
}

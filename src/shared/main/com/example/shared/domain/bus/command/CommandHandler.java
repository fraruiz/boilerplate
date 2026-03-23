package com.example.shared.domain.bus.command;

public interface CommandHandler<T extends Command> {
    void execute(T command);

    Class<T> command();
}

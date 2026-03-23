package com.example.shared.domain.bus.command;

public final class CommandNotRegisteredError extends InternalError {
    public CommandNotRegisteredError(Class<? extends Command> command) {
        super(String.format("The command <%s> hasn't a command handler associated", command.toString()));
    }
}

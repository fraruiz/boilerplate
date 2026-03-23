package com.example.shared.infrastructure.bus.command;

import com.example.shared.domain.bus.command.Command;
import com.example.shared.domain.bus.command.CommandBus;
import com.example.shared.domain.bus.command.CommandHandler;
import com.example.shared.domain.bus.command.CommandHandlerExecutionError;
import com.example.shared.infrastructure.ioc.IocContainer;
import com.google.inject.Inject;

public final class InMemoryCommandBus implements CommandBus {
    private final CommandHandlersInformation information;

    @Inject
    public InMemoryCommandBus() {
        this.information = new CommandHandlersInformation();
    }

    @Override
    public void execute(Command command) throws CommandHandlerExecutionError {
        try {
            Class<? extends CommandHandler> commandHandlerClass = information.search(command.getClass());

            CommandHandler handler = IocContainer.getSafeInstance(commandHandlerClass);

            handler.execute(command);
        } catch (Throwable error) {
            throw new CommandHandlerExecutionError(error);
        }
    }
}

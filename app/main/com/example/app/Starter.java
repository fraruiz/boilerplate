package com.example.app;

import com.example.analytics.shared.infrastructure.ioc.AnalyticsModule;
import com.example.app.server.ServerApplication;
import com.example.app.workers.WorkerApplication;
import com.example.shared.infrastructure.ioc.IocContainer;
import com.example.shared.infrastructure.ioc.SharedModule;
import com.google.inject.Guice;

public class Starter {
    static void main(String[] args) {
        if (args.length < 1) {
            throw new RuntimeException("There are not enough arguments");
        }

        String commandName = args[0];
        boolean isServerCommand = commandName.equals("server");

        IocContainer.addInjector(Guice.createInjector(new SharedModule(), new AnalyticsModule()));

        if (isServerCommand) {
            ServerApplication.execute();
        } else {
            WorkerApplication.execute();
        }
    }
}

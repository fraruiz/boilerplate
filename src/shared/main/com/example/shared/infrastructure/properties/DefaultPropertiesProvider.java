package com.example.shared.infrastructure.properties;

import com.example.shared.domain.env.Environment;
import com.example.shared.domain.properties.PropertiesProvider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

public final class DefaultPropertiesProvider implements PropertiesProvider {
    private static final String ENV_KEY       = "ENVIRONMENT";
    private static final String DEFAULT_ENV   = "test";
    private static final String FILE_PATTERN  = "application-%s.properties";

    private final Properties properties;

    public DefaultPropertiesProvider(Environment environment) {
        this.properties = new Properties();

        String env  = environment.execute(ENV_KEY, DEFAULT_ENV);
        String envFile = String.format(FILE_PATTERN, env);

        try {
            loadAll("application.properties");
            loadAll(envFile);
        } catch (IOException e) {
            throw new InternalError("Cannot load properties files");
        }
    }

    private void loadAll(String file) throws IOException {
        Enumeration<URL> resources = Thread.currentThread()
                .getContextClassLoader()
                .getResources(file);

        while (resources.hasMoreElements()) {
            try (InputStream stream = resources.nextElement().openStream()) {
                properties.load(stream);
            }
        }
    }

    @Override
    public String execute(String key) {
        return properties.getProperty(key);
    }

    @Override
    public String execute(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}

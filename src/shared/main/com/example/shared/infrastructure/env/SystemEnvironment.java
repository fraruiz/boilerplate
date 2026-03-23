package com.example.shared.infrastructure.env;

import com.example.shared.domain.env.Environment;

public class SystemEnvironment implements Environment {
    @Override
    public String execute(String key) {
        return System.getenv(key);
    }

    @Override
    public String execute(String key, String defaultValue) {
        try {
            String value = this.execute(key);

            if (value == null) {
                return defaultValue;
            }

            return value;
        } catch (Exception e) {
            return defaultValue;
        }
    }
}

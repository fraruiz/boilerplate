package com.example.shared.domain.locks;

import java.io.Serializable;

public interface Locker {
    void execute(Serializable context,
                 Serializable resource,
                 Serializable key,
                 Runnable runnable);
}

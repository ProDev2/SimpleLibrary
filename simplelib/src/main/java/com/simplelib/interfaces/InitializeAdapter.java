package com.simplelib.interfaces;

import java.util.concurrent.atomic.AtomicBoolean;

public interface InitializeAdapter {
    AtomicBoolean initialized = new AtomicBoolean(true);

    default boolean isInitialized() {
        return this.initialized.get();
    }

    default void setInitialized() {
        setInitialized(true);
    }

    default void setInitialized(boolean initialized) {
        this.initialized.set(initialized);
    }
}

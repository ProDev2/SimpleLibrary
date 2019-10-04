package com.simplelib.interfaces;

import androidx.annotation.NonNull;

import java.util.concurrent.atomic.AtomicBoolean;

public interface InitializeAdapter {
    String exceptionText = "The current state cannot be null";

    default boolean isInitialized() {
        AtomicBoolean state = getInitializedState();
        if (state == null)
            throw new NullPointerException(exceptionText);

        return state.get();
    }

    default void setInitialized() {
        setInitialized(true);
    }

    default void setInitialized(boolean initialized) {
        AtomicBoolean state = getInitializedState();
        if (state == null)
            throw new NullPointerException(exceptionText);

        state.set(initialized);
    }

    @NonNull AtomicBoolean getInitializedState();
}

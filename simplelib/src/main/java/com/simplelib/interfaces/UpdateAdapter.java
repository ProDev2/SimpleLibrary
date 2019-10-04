package com.simplelib.interfaces;

import androidx.annotation.NonNull;

import java.util.concurrent.atomic.AtomicBoolean;

public interface UpdateAdapter extends Updatable {
    String exceptionText = "The current state cannot be null";

    default boolean needsUpdate() {
        AtomicBoolean state = getNeedsUpdateState();
        if (state == null)
            throw new NullPointerException(exceptionText);

        return state.get();
    }

    default void setNeedsUpdate(boolean needsUpdate) {
        AtomicBoolean state = getNeedsUpdateState();
        if (state == null)
            throw new NullPointerException(exceptionText);

        state.set(needsUpdate);
    }

    default void update() {
        update(false);
    }

    default void update(boolean notify) {
        AtomicBoolean state = getNeedsUpdateState();
        if (state == null)
            throw new NullPointerException(exceptionText);

        if (!notify && !state.get()) return;
        state.set(false);

        onUpdate();
    }

    @NonNull AtomicBoolean getNeedsUpdateState();
}

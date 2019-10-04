package com.simplelib.interfaces;

import androidx.annotation.NonNull;

import java.util.concurrent.atomic.AtomicBoolean;

public interface VisibilityAdapter extends InitializeAdapter {
    String exceptionText = "The current state cannot be null";

    default void setDefVisibility(boolean visible) {
        AtomicBoolean state = getVisibleState();
        if (state == null)
            throw new NullPointerException(exceptionText);

        state.set(visible);
    }

    default boolean isShown() {
        AtomicBoolean state = getVisibleState();
        if (state == null)
            throw new NullPointerException(exceptionText);

        return state.get() && isInitialized();
    }

    default boolean isHidden() {
        AtomicBoolean state = getVisibleState();
        if (state == null)
            throw new NullPointerException(exceptionText);

        return !state.get() || !isInitialized();
    }

    default boolean getVisibility() {
        AtomicBoolean state = getVisibleState();
        if (state == null)
            throw new NullPointerException(exceptionText);

        return state.get();
    }

    default void updateVisibility() {
        AtomicBoolean state = getVisibleState();
        if (state == null)
            throw new NullPointerException(exceptionText);

        setVisibility(state.get(), true);
    }

    default void setVisibility(boolean visible) {
        setVisibility(visible, false);
    }

    default void setVisibility(boolean visible, boolean notify) {
        AtomicBoolean state = getVisibleState();
        if (state == null)
            throw new NullPointerException(exceptionText);

        boolean changed = state.get() != visible;
        state.set(visible);

        if (!isInitialized()) return;

        onVisibilitySet(visible);

        if (changed || notify)
            onVisibilityChanged(visible);
    }

    @NonNull AtomicBoolean getVisibleState();

    void onVisibilitySet(boolean visible);
    void onVisibilityChanged(boolean visible);
}

package com.simplelib.interfaces;

import java.util.concurrent.atomic.AtomicBoolean;

public interface VisibilityAdapter extends InitializeAdapter {
    AtomicBoolean visible = new AtomicBoolean(true);

    default void setDefVisibility(boolean visible) {
        this.visible.set(visible);
    }

    default boolean isShown() {
        return this.visible.get() && isInitialized();
    }

    default boolean isHidden() {
        return !this.visible.get() || !isInitialized();
    }

    default boolean getVisibility() {
        return this.visible.get();
    }

    default void updateVisibility() {
        setVisibility(this.visible.get(), true);
    }

    default void setVisibility(boolean visible) {
        setVisibility(visible, false);
    }

    default void setVisibility(boolean visible, boolean notify) {
        boolean changed = this.visible.get() != visible;
        this.visible.set(visible);

        if (!isInitialized()) return;

        onVisibilitySet(visible);

        if (changed || notify)
            onVisibilityChanged(visible);
    }

    void onVisibilitySet(boolean visible);
    void onVisibilityChanged(boolean visible);
}

package com.simplelib.interfaces;

import java.util.concurrent.atomic.AtomicBoolean;

public interface UpdateAdapter extends Updatable {
    AtomicBoolean needsUpdate = new AtomicBoolean(true);

    default boolean needsUpdate() {
        return this.needsUpdate.get();
    }

    default void setNeedsUpdate(boolean needsUpdate) {
        this.needsUpdate.set(needsUpdate);
    }

    default void update() {
        update(false);
    }

    default void update(boolean notify) {
        if (!notify && !this.needsUpdate.get()) return;
        this.needsUpdate.set(false);

        onUpdate();
    }
}

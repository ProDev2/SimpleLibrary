/*
 * Copyright (c) 2020 ProDev+ (Pascal Gerner).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

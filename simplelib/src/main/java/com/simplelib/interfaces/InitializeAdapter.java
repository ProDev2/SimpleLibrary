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

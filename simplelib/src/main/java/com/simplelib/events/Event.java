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

package com.simplelib.events;

import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.simplelib.builder.PathBuilder;

public class Event implements IEvent {
    protected final String key;
    protected final boolean noKey;

    private OnEventListener onEventListener;

    public Event() {
        this.key = null;
        this.noKey = true;
    }

    public Event(String key) {
        if (TextUtils.isEmpty(key))
            throw new IllegalArgumentException("Key cannot be empty");

        key = PathBuilder.format(key);
        if (TextUtils.isEmpty(key) || !PathBuilder.isSinglePart(key))
            throw new IllegalArgumentException("Key is invalid");

        this.key = key;
        this.noKey = false;
    }

    public final String getKey() {
        return key;
    }

    public boolean isNoKey() {
        return noKey;
    }

    @Override
    public final boolean invoke(String key, Bundle args) {
        key = PathBuilder.format(key);
        if (!noKey && TextUtils.isEmpty(key))
            return false;
        if (args == null)
            args = new Bundle();

        try {
            return onDispatchEvent(key, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean onDispatchEvent(@NonNull String key, @NonNull Bundle args) {
        key = PathBuilder.format(key);
        if (!noKey) {
            if (TextUtils.isEmpty(key))
                return false;

            String part = PathBuilder.get(key);
            if (TextUtils.isEmpty(part) || !part.equals(this.key))
                return false;
        }

        return onEvent(key, args);
    }

    public boolean onEvent(@NonNull String key, @NonNull Bundle args) {
        if (onEventListener != null)
            return onEventListener.onEvent(key, args);
        return false;
    }

    public final OnEventListener getOnEventListener() {
        return onEventListener;
    }

    public final void setOnEventListener(OnEventListener onEventListener) {
        this.onEventListener = onEventListener;
    }

    public interface OnEventListener {
        boolean onEvent(@NonNull String key, @NonNull Bundle args);
    }
}
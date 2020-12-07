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

package com.simplelib.builder;

public abstract class NestedBuilder<T, V> {
    private T parentBuilder;
    private OnResultListener<V> onResultListener;

    public <P extends NestedBuilder<T, V>> P withParentBuilder(T parentBuilder, OnResultListener<V> onResultListener) {
        this.parentBuilder = parentBuilder;
        this.onResultListener = onResultListener;

        return (P) this;
    }

    public T done() {
        if (onResultListener != null) {
            V value = build();
            onResultListener.onResult(value);
        }

        return parentBuilder;
    }

    public abstract V build();

    public interface OnResultListener<V> {
        void onResult(V result);
    }
}

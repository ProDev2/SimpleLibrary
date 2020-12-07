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

package com.simplelib.container;

import com.simplelib.adapter.SimpleRecyclerAdapter;
import com.simplelib.adapter.SimpleRecyclerFilterAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class SimpleFilter<V, E> {
    private SimpleRecyclerFilterAdapter<V, E> adapter;

    public SimpleFilter() {}

    public void setAdapter(SimpleRecyclerFilterAdapter<V, E> adapter) {
        this.adapter = adapter;
    }

    public SimpleRecyclerFilterAdapter<V, E> getAdapter() {
        return adapter;
    }

    public void reload() {
        if (adapter != null) {
            adapter.setFilter(this, false);
            adapter.reload();
        }
    }

    public void update() {
        if (adapter != null) {
            adapter.setFilter(this, false);
            adapter.update();
        }
    }

    public List<V> getList() {
        if (adapter != null)
            return adapter.getList();
        else
            return new ArrayList<>();
    }

    public SimpleRecyclerAdapter.Provider<V, E> getProvider() {
        if (adapter != null)
            return adapter.getProvider();
        else
            return null;
    }

    public abstract boolean filter(V value);
}

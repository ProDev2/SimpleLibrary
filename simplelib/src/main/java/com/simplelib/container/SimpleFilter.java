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

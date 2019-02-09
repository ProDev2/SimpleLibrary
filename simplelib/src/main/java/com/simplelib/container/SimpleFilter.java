package com.simplelib.container;

import com.simplelib.adapter.SimpleRecyclerFilterAdapter;

import java.util.ArrayList;

public abstract class SimpleFilter<V> {
    private SimpleRecyclerFilterAdapter<V> adapter;

    public SimpleFilter() {}

    public void setAdapter(SimpleRecyclerFilterAdapter<V> adapter) {
        if (adapter != null) this.adapter = adapter;
    }

    public SimpleRecyclerFilterAdapter<V> getAdapter() {
        return adapter;
    }

    public void update() {
        if (adapter != null) {
            adapter.setFilter(this);
            adapter.updateFilter();
        }
    }

    public void reload() {
        if (adapter != null) {
            adapter.setFilter(this);
            adapter.reload();
        }
    }

    public ArrayList<V> getList() {
        if (adapter != null)
            return adapter.getList();
        else
            return new ArrayList<>();
    }

    public abstract boolean filter(V value);
}

package com.simplelib.container;

import com.simplelib.adapter.SimpleRecyclerFilterAdapter;

import java.util.ArrayList;

public abstract class SimpleFilter<V> {
    private SimpleRecyclerFilterAdapter<V> adapter;

    public SimpleFilter() {}

    public void setAdapter(SimpleRecyclerFilterAdapter<V> adapter) {
        if (adapter != null) this.adapter = adapter;
    }

    public void update() {
        adapter.setFilter(this);
        if (adapter != null) adapter.updateFilter();
    }

    public ArrayList<V> getList() {
        if (adapter != null)
            return adapter.getList();
        else
            return new ArrayList<>();
    }

    public abstract boolean filter(V value);
}

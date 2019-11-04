package com.simplelib.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.simplelib.container.SimpleFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class SimpleRecyclerFilterAdapter<V, E> extends SimpleRecyclerAdapter<V, E> {
    private SimpleFilter<V, E> filter;

    private List<V> unfilteredList;
    private List<V> filteredList;

    public SimpleRecyclerFilterAdapter() {
        this (null);
    }

    public SimpleRecyclerFilterAdapter(List<V> list) {
        super();

        init();

        if (list != null) {
            unfilteredList = list;
            filteredList.addAll(unfilteredList);
        }

        super.setList(filteredList, false);

        try {
            setFilter(applyFilter(), false);
        } catch (Exception e) {
        }

        reload();
    }

    private void init() {
        unfilteredList = new ArrayList<>();
        filteredList = new ArrayList<>();
    }

    public SimpleFilter<V, E> applyFilter() {
        return null;
    }

    @Override
    public void applyTo(SimpleRecyclerAdapter<V, E> src) {
        try {
            if (src != null) {
                if (src instanceof SimpleRecyclerFilterAdapter) {
                    SimpleRecyclerFilterAdapter<V, E> srcFilterAdapter = (SimpleRecyclerFilterAdapter<V, E>) src;

                    srcFilterAdapter.filter = filter;
                }

                List<V> list = new ArrayList<>();
                if (unfilteredList != null)
                    list.addAll(unfilteredList);
                src.setList(unfilteredList, true);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void applyTo(SimpleRecyclerAdapter<V, E> src, boolean update) {
        try {
            if (src != null) {
                if (src instanceof SimpleRecyclerFilterAdapter) {
                    SimpleRecyclerFilterAdapter<V, E> srcFilterAdapter = (SimpleRecyclerFilterAdapter<V, E>) src;

                    srcFilterAdapter.filter = filter;
                }

                List<V> list = new ArrayList<>();
                if (unfilteredList != null)
                    list.addAll(unfilteredList);
                src.setList(unfilteredList, update);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        boolean setup = getRecyclerView() == null && recyclerView != null;

        super.onAttachedToRecyclerView(recyclerView);

        try {
            if (setup && recyclerView != null)
                reload();
        } catch (Exception e) {
        }
    }

    @Override
    public List<V> getList() {
        return unfilteredList;
    }

    @Override
    public void setList(List<V> list) {
        if (list == null)
            list = new ArrayList<>();
        unfilteredList = list;

        super.setList(filteredList, false);
        reload();
    }

    @Override
    public void setList(List<V> list, boolean update) {
        if (list == null)
            list = new ArrayList<>();
        unfilteredList = list;

        super.setList(filteredList, false);
        if (update)
            reload();
    }

    @Override
    public void add(V value) {
        unfilteredList.add(value);
        update();
    }

    @Override
    public void add(int index, V value) {
        unfilteredList.add(index, value);
        update();
    }

    @Override
    public V get(int pos) {
        if (pos >= 0 && pos < unfilteredList.size())
            return unfilteredList.get(pos);
        else
            return null;
    }

    @Override
    public V getAtAdapterPos(int pos) {
        if (pos >= 0 && pos < filteredList.size())
            return filteredList.get(pos);
        else
            return null;
    }

    @Override
    public int getAdapterItemPos(V value) {
        if (filteredList.contains(value))
            return filteredList.indexOf(value);
        else
            return -1;
    }

    @Override
    public int getItemPos(V value) {
        return unfilteredList.indexOf(value);
    }

    @Override
    public void move(int posFrom, int posTo) {
        try {
            if (posFrom >= 0 && posTo >= 0 && posFrom < unfilteredList.size() && posTo < unfilteredList.size() && posFrom != posTo) {
                V valueFrom = unfilteredList.get(posFrom);
                V valueTo = unfilteredList.get(posTo);

                swapList(unfilteredList, posFrom, posTo);

                int indexFrom = filteredList.indexOf(valueFrom);
                int indexTo = filteredList.indexOf(valueTo);

                if (indexFrom >= 0 && indexTo >= 0 && indexFrom < filteredList.size() && indexTo < filteredList.size())
                    super.move(indexFrom, indexTo);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public boolean contains(V value) {
        return unfilteredList.contains(value);
    }

    @Override
    public void remove(int pos) {
        if (pos >= 0 && pos < unfilteredList.size())
            unfilteredList.remove(pos);
        update();
    }

    @Override
    public void remove(V value) {
        if (unfilteredList.contains(value))
            unfilteredList.remove(value);
        update();
    }

    @Override
    public void clear() {
        unfilteredList.clear();
        update();
    }

    @Override
    public void sort(Comparator<? super V> comparator, boolean update) {
        try {
            if (comparator != null) {
                Collections.sort(unfilteredList, comparator);

                if (update)
                    reload();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public int getListSize() {
        return unfilteredList.size();
    }

    @Override
    public synchronized void reload() {
        try {
            if (filter != null)
                filter.setAdapter(this);
        } catch (Exception e) {
        }

        try {
            filteredList.clear();

            if (filter != null) {
                for (V item : unfilteredList) {
                    try {
                        boolean add = filter.filter(item);
                        if (add) filteredList.add(item);
                    } catch (Exception e) {
                    }
                }
            } else {
                filteredList.addAll(unfilteredList);
            }

            notifyDataSetChanged();
        } catch (Exception e) {
        }
    }

    @Override
    public void scrollUp() {
        try {
            if (filteredList.size() > 0)
                super.scrollToPosition(0);
        } catch (Exception e) {
        }
    }

    @Override
    public void scrollDown() {
        try {
            if (filteredList.size() > 0)
                super.scrollToPosition(filteredList.size() - 1);
        } catch (Exception e) {
        }
    }

    @Override
    public void scrollToPosition(int pos) {
        super.scrollToPosition(pos);
    }

    @Override
    public void scrollToPosition(V item) {
        super.scrollToPosition(item);
    }

    @Override
    public void scrollToPosition(int pos, boolean animate) {
        try {
            if (pos >= 0 && pos < unfilteredList.size()) {
                V value = unfilteredList.get(pos);
                if (filteredList.contains(value))
                    super.scrollToPosition(filteredList.indexOf(value), animate);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void scrollToPosition(V item, boolean animate) {
        super.scrollToPosition(item, animate);
    }

    public synchronized void update() {
        try {
            if (filter != null)
                filter.setAdapter(this);
        } catch (Exception e) {
        }

        try {
            for (V item : unfilteredList) {
                boolean add = true;
                if (filter != null) add = filter.filter(item);

                if (add && !filteredList.contains(item))
                    addItemToList(item);
                else if (!add && filteredList.contains(item))
                    removeItemFromList(item);
                else if (filteredList.contains(item))
                    moveItemInList(item);
            }
        } catch (Exception e) {
        }

        try {
            List<V> removeList = new ArrayList<>();
            for (V item : filteredList) {
                if (!unfilteredList.contains(item))
                    removeList.add(item);
            }
            for (V removeItem : removeList)
                super.remove(removeItem);
            removeList.clear();
        } catch (Exception e) {
        }
    }

    private void addItemToList(V item) {
        if (!filteredList.contains(item)) {
            V insertAfter = null;

            boolean found = false;
            for (V checkItem : unfilteredList) {
                if (!found && filteredList.contains(checkItem) && !item.equals(checkItem))
                    insertAfter = checkItem;
                else if (!found && item.equals(checkItem))
                    found = true;
            }

            try {
                if (insertAfter == null && filteredList.size() <= 0)
                    super.add(item);
                else if (insertAfter == null && filteredList.size() > 0)
                    super.add(0, item);
                else if (insertAfter != null && filteredList.contains(insertAfter)) {
                    int index = filteredList.indexOf(insertAfter) + 1;
                    if (index < filteredList.size())
                        super.add(index, item);
                    else
                        super.add(item);
                }
            } catch (Exception e) {
                super.add(item);
            }
        }
    }

    private void removeItemFromList(V item) {
        try {
            if (filteredList.contains(item))
                super.remove(item);
        } catch (Exception e) {
            super.remove(item);
        }
    }

    private void moveItemInList(V item) {
        if (filteredList.contains(item)) {
            V insertAfter = null;

            boolean found = false;
            for (V checkItem : unfilteredList) {
                if (!found && filteredList.contains(checkItem) && !item.equals(checkItem))
                    insertAfter = checkItem;
                else if (!found && item.equals(checkItem))
                    found = true;
            }

            try {
                if (insertAfter == null)
                    super.move(filteredList.indexOf(item), 0);
                else if (insertAfter != null && filteredList.contains(insertAfter)) {
                    int index = filteredList.indexOf(insertAfter) + 1;
                    super.move(filteredList.indexOf(item), index);
                }
            } catch (Exception e) {
            }
        }
    }

    public SimpleFilter<V, E> getFilter() {
        if (filter != null) filter.setAdapter(this);
        return filter;
    }

    public void setFilter(SimpleFilter<V, E> filter) {
        setFilter(filter, true);
    }

    public void setFilter(SimpleFilter<V, E> filter, boolean update) {
        if (this.filter == filter)
            return;

        try {
            if (this.filter != null)
                this.filter.setAdapter(null);
        } catch (Exception e) {
        }

        this.filter = filter;

        try {
            if (this.filter != null)
                this.filter.setAdapter(this);
        } catch (Exception e) {
        }

        if (update)
            update();
    }
}

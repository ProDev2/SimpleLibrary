package com.simplelib.adapter;

import android.support.v7.widget.RecyclerView;

import com.simplelib.container.SimpleFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public abstract class SimpleRecyclerFilterAdapter<V> extends SimpleRecyclerAdapter<V> {
    private SimpleFilter<V> filter;

    private ArrayList<V> unfilteredList;
    private ArrayList<V> filteredList;

    public SimpleRecyclerFilterAdapter() {
        this (null);
    }

    public SimpleRecyclerFilterAdapter(ArrayList<V> list) {
        super();

        init();

        if (list != null) {
            unfilteredList = list;
            filteredList.addAll(unfilteredList);
        }

        super.setList(filteredList);

        filter = applyFilter();
    }

    private void init() {
        unfilteredList = new ArrayList<>();
        filteredList = new ArrayList<>();
    }

    public SimpleFilter<V> applyFilter() {
        return null;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        try {
            if (recyclerView != null)
                updateFilter(false);
        } catch (Exception e) {
        }
    }

    @Override
    public ArrayList<V> getList() {
        return unfilteredList;
    }

    @Override
    public void setList(ArrayList<V> list) {
        unfilteredList = list;

        updateFilter();

        super.setList(filteredList);
    }

    @Override
    public void setList(ArrayList<V> list, boolean update) {
        unfilteredList = list;

        updateFilter();

        super.setList(filteredList, update);
    }

    @Override
    public void add(V value) {
        unfilteredList.add(value);
        updateFilter();
    }

    @Override
    public void add(int index, V value) {
        unfilteredList.add(index, value);
        updateFilter();
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
            if (posFrom >= 0 && posTo >= 0 && posFrom < unfilteredList.size() && posTo < unfilteredList.size()) {
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
        updateFilter();
    }

    @Override
    public void remove(V value) {
        if (unfilteredList.contains(value))
            unfilteredList.remove(value);
        updateFilter();
    }

    @Override
    public void clear() {
        unfilteredList.clear();
        updateFilter();
    }

    @Override
    public void sort(Comparator<? super V> comparator) {
        try {
            if (comparator != null) {
                Collections.sort(unfilteredList, comparator);
                updateFilter();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public int getListSize() {
        return unfilteredList.size();
    }

    @Override
    public void reload() {
        try {
            filteredList.clear();
            notifyDataSetChanged();

            runAfterUpdate(new Runnable() {
                @Override
                public void run() {
                    updateFilter(false);
                }
            });
        } catch (Exception e) {
        }
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

    public void updateFilter() {
        updateFilter(true);
    }

    public void updateFilter(boolean animate) {
        if (animate) {
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
                ArrayList<V> removeList = new ArrayList<>();
                for (V item : filteredList) {
                    if (!unfilteredList.contains(item))
                        removeList.add(item);
                }
                for (V removeItem : removeList)
                    super.remove(removeItem);
                removeList.clear();
            } catch (Exception e) {
            }
        } else {
            try {
                filteredList.clear();
                notifyDataSetChanged();

                for (V item : unfilteredList) {
                    boolean add = true;
                    if (filter != null) add = filter.filter(item);

                    if (add)
                        filteredList.add(item);
                }

                notifyDataSetChanged();
            } catch (Exception e) {
            }
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

    public SimpleFilter<V> getFilter() {
        if (filter != null) filter.setAdapter(this);
        return filter;
    }

    public void setFilter(SimpleFilter<V> filter) {
        this.filter = filter;
        if (this.filter != null)
            this.filter.setAdapter(this);
        updateFilter();
    }
}

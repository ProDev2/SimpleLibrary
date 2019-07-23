package com.simplelib.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.Collections;
import java.util.List;

public class SimpleItemTouchHelper extends ItemTouchHelper.Callback {
    private RecyclerView recyclerView;
    private SimpleRecyclerAdapter adapter;
    private SimpleSettings settings;

    private ItemTouchHelper touchHelper;

    public SimpleItemTouchHelper(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.settings = new SimpleSettings();

        if (recyclerView != null)
            applyTo(recyclerView);
    }

    public SimpleItemTouchHelper(RecyclerView recyclerView, SimpleRecyclerAdapter adapter) {
        this(recyclerView);
        this.adapter = adapter;
    }

    public static SimpleItemTouchHelper apply(RecyclerView recyclerView) {
        return new SimpleItemTouchHelper(recyclerView);
    }

    public static SimpleItemTouchHelper apply(RecyclerView recyclerView, SimpleRecyclerAdapter adapter) {
        return new SimpleItemTouchHelper(recyclerView, adapter);
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public SimpleRecyclerAdapter getAdapter() {
        fetch();
        return adapter;
    }

    public SimpleSettings getSettings() {
        return settings;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return settings.isLongPressDragEnabled();
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return settings.isItemViewSwipeEnabled();
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = settings.getDragFlags();
        int swipeFlags = settings.getSwipeFlags();

        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        fetch();

        if (adapter == null) return true;

        int from = adapter.getItemPos(adapter.getAtAdapterPos(viewHolder.getAdapterPosition()));
        int to = adapter.getItemPos(adapter.getAtAdapterPos(target.getAdapterPosition()));

        if (from < 0 || to < 0 || from >= adapter.getListSize() || to >= adapter.getListSize()) return true;

        try {
            if (adapter.getList() != null && adapter.getListSize() > 0)
                adapter.move(from, to);
        } catch (Exception e) {
        }

        onMoveItem(from, to);
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        fetch();

        if (adapter == null) return;

        int pos = adapter.getItemPos(adapter.getAtAdapterPos(viewHolder.getAdapterPosition()));
        if (pos < 0 || pos >= adapter.getListSize()) return;

        onSwipeItem(pos, direction);

        if (settings.deleteItemOnSwipe()) {
            try {
                if (adapter.getList() != null && adapter.getListSize() > 0)
                    adapter.remove(pos);
            } catch (Exception e) {
            }

            onRemoveItem(pos, direction);
        }
    }

    private void fetch() {
        try {
            if (recyclerView != null) {
                if (recyclerView.getAdapter() instanceof SimpleRecyclerAdapter) {
                    this.adapter = (SimpleRecyclerAdapter) recyclerView.getAdapter();
                }
            }
        } catch (Exception e) {
        }
    }

    public void applyTo(RecyclerView recyclerView) {
        if (recyclerView != null) {
            this.recyclerView = recyclerView;
            fetch();

            if (touchHelper == null) {
                touchHelper = new ItemTouchHelper(this);
            }
            touchHelper.attachToRecyclerView(recyclerView);
        }
    }

    public void swapList(List<?> list, int fromPos, int toPos) {
        if (fromPos < toPos) {
            for (int pos = fromPos; pos < toPos; pos++) {
                Collections.swap(list, pos, pos + 1);
            }
        } else {
            for (int pos = fromPos; pos > toPos; pos--) {
                Collections.swap(list, pos, pos - 1);
            }
        }
    }

    public void onSwipeItem(int position, int direction) {
    }

    public void onRemoveItem(int position, int direction) {
    }

    public void onMoveItem(int fromPos, int toPos) {
    }
}

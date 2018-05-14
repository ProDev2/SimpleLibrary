package com.simplelib.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.ArrayList;
import java.util.Collections;

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

        if (adapter != null) {
            try {
                if (adapter.getList() != null && adapter.getListSize() > 0)
                    adapter.swapList(adapter.getList(), viewHolder.getAdapterPosition(), target.getAdapterPosition());

                adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            } catch (Exception e) {
            }
        }

        onMoveItem(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        fetch();

        onSwipeItem(viewHolder.getAdapterPosition(), direction);

        if (settings.deleteItemOnSwipe() && adapter != null) {
            try {
                if (adapter.getList() != null && adapter.getListSize() > 0) {
                    adapter.remove(viewHolder.getAdapterPosition());
                }
            } catch (Exception e) {
            }

            onRemoveItem(viewHolder.getAdapterPosition(), direction);
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

    public void swapList(ArrayList<?> list, int fromPos, int toPos) {
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

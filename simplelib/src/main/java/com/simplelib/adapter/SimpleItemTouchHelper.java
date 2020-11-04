package com.simplelib.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import java.util.Collections;
import java.util.List;

@SuppressWarnings({
        "unused",
        "UnusedReturnValue"
})
public class SimpleItemTouchHelper extends ItemTouchHelper.Callback {
    private RecyclerView recyclerView;
    private SimpleRecyclerAdapter<?, ?> adapter;
    private SimpleSettings settings;

    private ItemTouchHelper touchHelper;

    public SimpleItemTouchHelper(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.settings = new SimpleSettings();

        if (recyclerView != null)
            applyTo(recyclerView);
    }

    public SimpleItemTouchHelper(RecyclerView recyclerView, SimpleRecyclerAdapter<?, ?> adapter) {
        this(recyclerView);
        this.adapter = adapter;
    }

    public static SimpleItemTouchHelper apply(RecyclerView recyclerView) {
        return new SimpleItemTouchHelper(recyclerView);
    }

    public static SimpleItemTouchHelper apply(RecyclerView recyclerView, SimpleRecyclerAdapter<?, ?> adapter) {
        return new SimpleItemTouchHelper(recyclerView, adapter);
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public SimpleRecyclerAdapter<?, ?> getAdapter() {
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
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = settings.getDragFlags();
        int swipeFlags = settings.getSwipeFlags();

        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        fetch();

        if (adapter == null)
            return false;

        int from = adapter.getPosAtAdapterPos(viewHolder.getAdapterPosition());
        int to = adapter.getPosAtAdapterPos(target.getAdapterPosition());

        if (from < 0 || to < 0 || from >= adapter.getListSize() || to >= adapter.getListSize())
            return false;

        try {
            boolean preMoved = onPreMoveItem(viewHolder, from, to);
            if (!preMoved) return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        try {
            if (adapter.getList() != null)
                adapter.move(from, to);
        } catch (Throwable tr) {
            tr.printStackTrace();
            return false;
        }

        try {
            return onMoveItem(viewHolder, from, to);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        fetch();

        if (adapter == null) return;

        int pos = adapter.getPosAtAdapterPos(viewHolder.getAdapterPosition());
        if (pos < 0 || pos >= adapter.getListSize()) return;

        try {
            onSwipeItem(viewHolder, pos, direction);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (settings.deleteItemOnSwipe()) {
            try {
                onPreRemoveItem(viewHolder, pos, direction);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (adapter.getList() != null && adapter.getListSize() > 0)
                    adapter.remove(pos);
            } catch (Exception ignored) {
            }

            try {
                onRemoveItem(viewHolder, pos, direction);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void fetch() {
        try {
            if (recyclerView != null) {
                if (recyclerView.getAdapter() instanceof SimpleRecyclerAdapter) {
                    this.adapter = (SimpleRecyclerAdapter<?, ?>) recyclerView.getAdapter();
                }
            }
        } catch (Exception ignored) {
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

    protected boolean onPreMoveItem(@NonNull RecyclerView.ViewHolder holder, int fromPos, int toPos) {
        try {
            holder.itemView.clearFocus();
        } catch (Throwable ignored) {
        }
        return true;
    }

    protected void onPreRemoveItem(@NonNull RecyclerView.ViewHolder holder, int position, int direction) {
        try {
            holder.itemView.clearFocus();
        } catch (Throwable ignored) {
        }
    }

    protected void onSwipeItem(@NonNull RecyclerView.ViewHolder holder, int position, int direction) {
    }

    protected void onRemoveItem(@NonNull RecyclerView.ViewHolder holder, int position, int direction) {
    }

    protected boolean onMoveItem(@NonNull RecyclerView.ViewHolder holder, int fromPos, int toPos) {
        return true;
    }
}

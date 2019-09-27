package com.simplelib.adapter;

import androidx.recyclerview.widget.ItemTouchHelper;

public class SimpleSettings {
    public static final int DRAG_UP_DOWN = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
    public static final int DRAG_LEFT_RIGHT = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
    public static final int DRAG = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

    public static final int SWIPE_START_END = ItemTouchHelper.START | ItemTouchHelper.END;

    private boolean isLongPressDragEnabled = true;
    private boolean isItemViewSwipeEnabled = true;

    private boolean deleteItemOnSwipe = true;

    private int dragFlags = DRAG;
    private int swipeFlags = SWIPE_START_END;

    public SimpleSettings() {
    }

    public boolean isLongPressDragEnabled() {
        return isLongPressDragEnabled;
    }

    public void setLongPressDragEnabled(boolean enabled) {
        this.isLongPressDragEnabled = enabled;
    }

    public boolean isItemViewSwipeEnabled() {
        return isItemViewSwipeEnabled;
    }

    public void setItemViewSwipeEnabled(boolean enabled) {
        this.isItemViewSwipeEnabled = enabled;
    }

    public int getDragFlags() {
        return dragFlags;
    }

    public void setDragFlags(int dragFlags) {
        this.dragFlags = dragFlags;
    }

    public int getSwipeFlags() {
        return swipeFlags;
    }

    public void setSwipeFlags(int swipeFlags) {
        this.swipeFlags = swipeFlags;
    }

    public void setDeleteItemOnSwipe(boolean deleteItemOnSwipe) {
        this.deleteItemOnSwipe = deleteItemOnSwipe;
    }

    public boolean deleteItemOnSwipe() {
        return deleteItemOnSwipe;
    }
}

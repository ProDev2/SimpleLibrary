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

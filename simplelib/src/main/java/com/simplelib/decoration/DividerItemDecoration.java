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

package com.simplelib.decoration;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    public static final int HORIZONTAL = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL = LinearLayoutManager.VERTICAL;

    private static final int[] ATTRS = new int[] {
            android.R.attr.listDivider
    };

    private Drawable divider;
    private int orientation;
    private boolean showAfter;
    private boolean showForLast;

    private Rect insets;

    public DividerItemDecoration(Context context, int orientation) {
        this(context, orientation, true, true);
    }

    public DividerItemDecoration(Context context, int orientation, boolean showAfter, boolean showForLast) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        divider = a.getDrawable(0);
        a.recycle();
        setOrientation(orientation);
        setShowAfter(showAfter);
        setShowForLast(showForLast);
    }

    public void setOrientation(int orientation) {
        if (orientation != VERTICAL && orientation != HORIZONTAL)
            throw new IllegalArgumentException("Invalid orientation");
        this.orientation = orientation;
    }

    public void setDrawable(Drawable divider) {
        if (divider == null)
            throw new IllegalArgumentException("Drawable cannot be null.");
        this.divider = divider;
    }

    public void setShowAfter(boolean showAfter) {
        this.showAfter = showAfter;
    }

    public void setShowForLast(boolean showForLast) {
        this.showForLast = showForLast;
    }

    public void setInsets(Rect insets) {
        this.insets = insets;
    }

    public void setVerticalInsets(int left, int right) {
        if (insets == null)
            insets = new Rect();
        insets.left = left;
        insets.right = right;
    }

    public void setHorizontalInsets(int top, int bottom) {
        if (insets == null)
            insets = new Rect();
        insets.top = top;
        insets.bottom = bottom;
    }

    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(canvas, parent, state);

        if (orientation == VERTICAL)
            drawVertical(canvas, parent);
        else
            drawHorizontal(canvas, parent);
    }

    public void drawVertical(Canvas canvas, RecyclerView parent) {
        if (divider == null || parent == null)
            return;

        int insetsLeft = 0;
        int insetsRight = 0;

        if (insets != null) {
            insetsLeft = insets.left;
            insetsRight = insets.right;
        }

        final int left = parent.getPaddingLeft() + insetsLeft;
        final int right = parent.getWidth() - parent.getPaddingRight() - insetsRight;

        if ((right - left) <= 0)
            return;

        int childCount = parent.getChildCount();
        if (!showForLast)
            childCount--;

        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top;
            final int bottom;
            if (showAfter) {
                top = child.getBottom() + params.bottomMargin;
                bottom = top + divider.getIntrinsicHeight();
            } else {
                bottom = child.getTop() - params.topMargin;
                top = bottom - divider.getIntrinsicHeight();
            }

            divider.setBounds(left, top, right, bottom);
            divider.draw(canvas);
        }
    }

    public void drawHorizontal(Canvas canvas, RecyclerView parent) {
        if (divider == null || parent == null)
            return;

        int insetsTop = 0;
        int insetsBottom = 0;

        if (insets != null) {
            insetsTop = insets.top;
            insetsBottom = insets.bottom;
        }

        final int top = parent.getPaddingTop() + insetsTop;
        final int bottom = parent.getHeight() - parent.getPaddingBottom() - insetsBottom;

        if ((bottom - top) <= 0)
            return;

        int childCount = parent.getChildCount();
        if (!showForLast)
            childCount--;

        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left;
            final int right;
            if (showAfter) {
                left = child.getRight() + params.rightMargin;
                right = left + divider.getIntrinsicHeight();
            } else {
                right = child.getLeft() - params.leftMargin;
                left = right - divider.getIntrinsicHeight();
            }

            divider.setBounds(left, top, right, bottom);
            divider.draw(canvas);
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (divider == null || parent == null)
            return;

        if (!showForLast) {
            int childCount = parent.getChildCount();
            int pos = parent.getChildAdapterPosition(view);
            if (pos >= (childCount - 1))
                return;
        }

        if (orientation == VERTICAL) {
            outRect.set(0, 0, 0, divider.getIntrinsicHeight());
        } else {
            outRect.set(0, 0, divider.getIntrinsicWidth(), 0);
        }
    }
}

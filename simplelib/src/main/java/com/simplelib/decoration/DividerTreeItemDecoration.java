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

import com.simplelib.struct.Tree;
import com.simplelib.struct.adapter.TreeAdapter;

public class DividerTreeItemDecoration extends RecyclerView.ItemDecoration {
    public static final int HORIZONTAL = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL = LinearLayoutManager.VERTICAL;

    private static final int[] ATTRS = new int[] {
            android.R.attr.listDivider
    };

    private Drawable divider;
    private int orientation;
    private boolean showAfter;
    private boolean showForFirst;
    private boolean showForLast;

    private int[] levels;

    private Rect insets;

    public DividerTreeItemDecoration(Context context, int orientation) {
        this(context, orientation, false, false, true);
    }

    public DividerTreeItemDecoration(Context context, int orientation, boolean showAfter, boolean showForFirst, boolean showForLast) {
        this(context, orientation, showAfter, showForFirst, showForLast, null);
    }

    public DividerTreeItemDecoration(Context context, int orientation, boolean showAfter, boolean showForFirst, boolean showForLast, int[] levels) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        divider = a.getDrawable(0);
        a.recycle();
        setOrientation(orientation);
        setShowAfter(showAfter);
        setShowForFirst(showForFirst);
        setShowForLast(showForLast);

        if (levels != null)
            setShowForLevels(levels);
        else
            setShowForAllLevels();
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

    public void setShowForFirst(boolean showForFirst) {
        this.showForFirst = showForFirst;
    }

    public void setShowForLast(boolean showForLast) {
        this.showForLast = showForLast;
    }

    public void setShowForAllLevels() {
        this.levels = null;
    }

    public void setShowForLevels(int[] levels) {
        this.levels = levels;
    }

    public void setShowForLevel(int level) {
        this.levels = new int[] {level};
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

        RecyclerView.Adapter adapter = parent.getAdapter();
        if (adapter == null || !(adapter instanceof TreeAdapter<?>))
            return;

        TreeAdapter<?> treeAdapter = (TreeAdapter<?>) adapter;
        int treeSize = adapter.getItemCount();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final int adapterPos;
            final Tree.Item treeItem;
            final int treeLevel;
            try {
                adapterPos = parent.getChildAdapterPosition(child);
                if (adapterPos == RecyclerView.NO_POSITION)
                    continue;

                treeItem = treeAdapter.getAtAdapterPos(adapterPos);
                if (treeItem == null)
                    continue;

                treeLevel = treeItem.getLevel();
            } catch (Exception e) {
                continue;
            }
            if ((!showForFirst && adapterPos <= 0) ||
                    (!showForLast && adapterPos >= treeSize - 1))
                continue;

            boolean hasLevel = levels == null;
            for (int index = 0; !hasLevel && index < levels.length; index++)
                hasLevel = levels[index] == treeLevel;
            if (!hasLevel)
                continue;

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

        RecyclerView.Adapter adapter = parent.getAdapter();
        if (adapter == null || !(adapter instanceof TreeAdapter<?>))
            return;

        TreeAdapter<?> treeAdapter = (TreeAdapter<?>) adapter;
        int treeSize = adapter.getItemCount();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final int adapterPos;
            final Tree.Item treeItem;
            final int treeLevel;
            try {
                adapterPos = parent.getChildAdapterPosition(child);
                if (adapterPos == RecyclerView.NO_POSITION)
                    continue;

                treeItem = treeAdapter.getAtAdapterPos(adapterPos);
                if (treeItem == null)
                    continue;

                treeLevel = treeItem.getLevel();
            } catch (Exception e) {
                continue;
            }
            if ((!showForFirst && adapterPos <= 0) ||
                    (!showForLast && adapterPos >= treeSize - 1))
                continue;

            boolean hasLevel = levels == null;
            for (int index = 0; !hasLevel && index < levels.length; index++)
                hasLevel = levels[index] == treeLevel;
            if (!hasLevel)
                continue;

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

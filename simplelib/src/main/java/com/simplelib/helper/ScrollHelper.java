package com.simplelib.helper;

import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;

public class ScrollHelper extends GestureHelper implements Runnable {
    //Adapter
    private ScrollAdapter adapter;

    //Configuration
    private boolean scrollEnabled;
    private float fps;

    private boolean scrollOnLongClick;

    private boolean reverseScrollAxis;
    private boolean swapScrollAxis;
    private boolean combineScrollAxis;

    private float scrollFactor;
    private float scrollOffset;

    //Scrolling
    private Handler handler;

    private boolean canScroll;

    public ScrollHelper() {
        this(null);
    }

    public ScrollHelper(ScrollAdapter adapter) {
        this.adapter = adapter;

        enabled = true;
        fixed = true;

        init();
    }

    private void init() {
        scrollEnabled = true;
        fps = 60f;

        scrollOnLongClick = false;

        reverseScrollAxis = false;
        swapScrollAxis = false;
        combineScrollAxis = false;

        scrollFactor = 0.08f;
        scrollOffset = 100f;
    }

    public ScrollHelper setScrollAdapter(ScrollAdapter adapter) {
        this.adapter = adapter;
        return this;
    }

    public ScrollHelper setScrollEnabled(boolean scrollEnabled) {
        this.scrollEnabled = scrollEnabled;
        return this;
    }

    public ScrollHelper setFps(float fps) {
        this.fps = fps;
        return this;
    }

    public ScrollHelper setScrollOnLongClick(boolean scrollOnLongClick) {
        this.scrollOnLongClick = scrollOnLongClick;
        return this;
    }

    public ScrollHelper setReverseScrollAxis(boolean reverseScrollAxis) {
        this.reverseScrollAxis = reverseScrollAxis;
        return this;
    }

    public ScrollHelper setSwapScrollAxis(boolean swapScrollAxis) {
        this.swapScrollAxis = swapScrollAxis;
        return this;
    }

    public ScrollHelper setCombineScrollAxis(boolean combineScrollAxis) {
        this.combineScrollAxis = combineScrollAxis;
        return this;
    }

    public ScrollHelper setScrollFactor(float scrollFactor) {
        this.scrollFactor = scrollFactor;
        return this;
    }

    public ScrollHelper setScrollOffset(float scrollOffset) {
        this.scrollOffset = scrollOffset;
        return this;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        return super.onTouch(view, event);
    }

    @Override
    public void onLongPress(MotionEvent event) {
        super.onLongPress(event);

        if (!isEnabled() || !scrollEnabled) return;
        if (scrollOnLongClick)
            startScroll();
    }

    @Override
    public boolean onPress(View view, float x, float y) {
        super.onPress(view, x, y);

        if (!isEnabled() || !scrollEnabled) return false;
        if (!scrollOnLongClick)
            startScroll();

        return isEnabled() && scrollEnabled;
    }

    @Override
    public boolean onRelease(View view, float x, float y, float distX, float distY) {
        super.onRelease(view, x, y, distX, distY);

        stopScroll();

        return isEnabled() && scrollEnabled;
    }

    public void startScroll() {
        stopScroll();

        if (adapter != null)
            adapter.setScrollingEnabled(false);
        canScroll = true;

        if (handler == null)
            handler = new Handler(Looper.getMainLooper());
        handler.removeCallbacks(this);
        handler.postDelayed(this, (long) (1000f / fps));
    }

    public void stopScroll() {
        if (adapter != null)
            adapter.setScrollingEnabled(true);
        canScroll = false;

        if (handler != null)
            handler.removeCallbacks(this);
    }

    @Override
    public void run() {
        if (!canScroll) return;

        if (isMoved())
            scroll();

        //Post next update
        if (canScroll)
            handler.postDelayed(this, (long) (1000f / fps));
    }

    public void scroll() {
        if (!isMoved()) return;

        try {
            float distX = getDistX();
            float distY = getDistY();

            distX = moveTowards(distX, 0, scrollOffset);
            distY = moveTowards(distY, 0, scrollOffset);

            if (reverseScrollAxis) {
                distX = -distX;
                distY = -distY;
            }

            float scrollDistX = distX * scrollFactor;
            float scrollDistY = distY * scrollFactor;

            if (combineScrollAxis) {
                float minScroll = Math.min(scrollDistX, scrollDistY);
                float maxScroll = Math.max(scrollDistX, scrollDistY);

                float scroll = makePositive(maxScroll) > makePositive(minScroll) ? maxScroll : minScroll;

                scrollDistX = scroll;
                scrollDistY = scroll;
            }

            if (!swapScrollAxis)
                onScrollBy(adapter, scrollDistX, scrollDistY);
            else
                onScrollBy(adapter, scrollDistY, scrollDistX);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private float makePositive(float n) {
        return n >= 0 ? n : -n;
    }

    private float moveTowards(float number, float target, float delta) {
        if (number == target) {
            number = target;
        } else if (number > target) {
            number -= delta;
            if (number < target)
                number = target;
        } else if (number < target) {
            number += delta;
            if (number > target)
                number = target;
        }
        return number;
    }

    public void onScrollBy(ScrollAdapter adapter, float scrollX, float scrollY) {
        try {
            if (adapter != null)
                adapter.onScrollBy(scrollX, scrollY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface ScrollAdapter {
        void setScrollingEnabled(boolean enabled);

        void onScrollBy(float distX, float distY);
    }
}
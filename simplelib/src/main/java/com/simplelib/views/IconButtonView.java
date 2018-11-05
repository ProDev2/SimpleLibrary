package com.simplelib.views;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.OvershootInterpolator;

public class IconButtonView extends IconView implements ValueAnimator.AnimatorUpdateListener {
    private ValueAnimator animator;

    private GestureDetector gestureDetector;

    private boolean selected;

    public IconButtonView(Context context) {
        super(context);
        init();
    }

    public IconButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IconButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        animator = ValueAnimator.ofInt(0, 180);
        animator.setDuration(500);
        animator.setInterpolator(new OvershootInterpolator());
        animator.addUpdateListener(this);

        gestureDetector = new GestureDetector(getContext(), new ButtonGestureListener());
    }

    public ValueAnimator getAnimator() {
        return animator;
    }

    public void setDuration(long duration) {
        if (animator != null)
            animator.setDuration(duration);
    }

    public void setInterpolator(TimeInterpolator interpolator) {
        if (animator != null)
            animator.setInterpolator(interpolator);
    }

    public void select() {
        select(true);
    }

    public void select(boolean animate) {
        if (animator != null && !selected) {
            if (animator.isRunning())
                animator.cancel();

            selected = true;
            try {
                boolean interrupt = onSelecting();
                if (interrupt)
                    return;
            } catch (Exception e) {
            }

            if (animate) {
                animator.setIntValues((int) getRotY(), 180);
                animator.start();
            } else {
                try {
                    onSelectedSideShown();
                } catch (Exception e) {
                }
                setRotY(180);
                redraw();
            }
        }
    }

    public void unselect() {
        unselect(true);
    }

    public void unselect(boolean animate) {
        if (animator != null && selected) {
            if (animator.isRunning())
                animator.cancel();

            selected = false;
            try {
                boolean interrupt = onUnselecting();
                if (interrupt)
                    return;
            } catch (Exception e) {
            }

            if (animate) {
                animator.setIntValues((int) getRotY(), 0);
                animator.start();
            } else {
                try {
                    onUnselectedSideShown();
                } catch (Exception e) {
                }
                setRotY(0);
                redraw();
            }
        }
    }

    public void toggle() {
        toggle(true);
    }

    public void toggle(boolean animate) {
        if (selected)
            unselect(animate);
        else
            select(animate);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        try {
            int value = (int) animation.getAnimatedValue();

            try {
                if (value <= 90)
                    onUnselectedSideShown();
                else
                    onSelectedSideShown();
            } catch (Exception e) {
            }

            setRotY(value);
            redraw();
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    protected boolean onSelecting() {
        return false;
    }

    protected boolean onUnselecting() {
        return false;
    }

    protected void onSelectedSideShown() {
    }

    protected void onUnselectedSideShown() {
    }

    private final class ButtonGestureListener extends GestureDetector.SimpleOnGestureListener {
        private boolean moved;

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (!moved)
                performClick();
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            performLongClick();
        }

        @Override
        public boolean onDown(MotionEvent e) {
            moved = false;
            toggle(true);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!moved)
                toggle(true);
            moved = true;
            return true;
        }
    }
}

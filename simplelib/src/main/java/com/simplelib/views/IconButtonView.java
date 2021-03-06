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
    private int currentPos;

    private GestureDetector gestureDetector;
    private ButtonGestureListener gestureListener;

    private boolean selected;

    private boolean noFlip;

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

        gestureListener = new ButtonGestureListener();
        gestureDetector = new GestureDetector(getContext(), gestureListener);
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

    public boolean isIconSelected() {
        return selected;
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
                animator.setIntValues(currentPos, 180);
                animator.start();
            } else {
                try {
                    onSelectedSideShown(false);
                } catch (Exception e) {
                }
                currentPos = 180;
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
                animator.setIntValues(currentPos, 0);
                animator.start();
            } else {
                try {
                    onUnselectedSideShown(false);
                } catch (Exception e) {
                }
                currentPos = 0;
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

    public void setNoFlip(boolean noFlip) {
        this.noFlip = noFlip;
        redraw();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        try {
            int value = (int) animation.getAnimatedValue();

            try {
                if (value <= 90)
                    onUnselectedSideShown(true);
                else
                    onSelectedSideShown(true);
            } catch (Exception e) {
            }

            currentPos = value;
            redraw();
        } catch (Exception e) {
        }
    }

    @Override
    public void redraw() {
        if (!noFlip) {
            setRotY(currentPos);
        } else {
            if (currentPos <= 90)
                setRotY(currentPos);
            else
                setRotY(currentPos + 180);
        }

        super.redraw();
    }

    protected boolean onSelecting() {
        return false;
    }

    protected boolean onUnselecting() {
        return false;
    }

    protected void onSelectedSideShown(boolean animate) {
    }

    protected void onUnselectedSideShown(boolean animate) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (gestureListener != null)
                    gestureListener.onDown();
                break;

            case MotionEvent.ACTION_UP:
                if (gestureListener != null)
                    gestureListener.onUp();
                break;
        }

        return true;
    }

    private final class ButtonGestureListener extends GestureDetector.SimpleOnGestureListener {
        private boolean holding;
        private boolean moved;
        private boolean longClicked;

        @Override
        public void onLongPress(MotionEvent e) {
            if (!longClicked) {
                longClicked = true;
                performLongClick();
            }
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            onMove();
            return true;
        }

        public void onUp() {
            if (holding) {
                holding = false;
                if (!moved && !longClicked)
                    performClick();
            }
        }

        public void onDown() {
            if (!holding) {
                holding = true;
                moved = false;
                longClicked = false;
                toggle(true);
            }
        }

        public void onMove() {
            if (!moved)
                toggle(true);
            moved = true;
        }
    }
}

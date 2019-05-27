package com.simplelib.helper;

import android.view.MotionEvent;
import android.view.View;

public class TouchHelper extends BasicTouchHelper {
    private static final float DEFAULT_MIN_MOVEMENT = 50;

    protected boolean enabled;
    protected boolean fixed;

    private boolean pressed;
    private boolean moved;

    private float startX, startY;
    private float touchX, touchY;
    private float moveX, moveY;
    private float distX, distY;

    private float minMovement = DEFAULT_MIN_MOVEMENT;

    public TouchHelper() {
        enabled = true;
        fixed = false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    public void setMinMovement(int minMovement) {
        this.minMovement = minMovement;
    }

    public boolean isPressed() {
        return pressed;
    }

    public boolean isMoved() {
        return moved;
    }

    public float getStartX() {
        return startX;
    }

    public float getStartY() {
        return startY;
    }

    public float getTouchX() {
        return touchX;
    }

    public float getTouchY() {
        return touchY;
    }

    public float getDistX() {
        return distX;
    }

    public float getDistY() {
        return distY;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (!enabled) return false;

        boolean handled = false;

        handled |= super.onTouch(view, event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!pressed) {
                    pressed = true;
                    moved = false;

                    startX = event.getRawX();
                    startY = event.getRawY();

                    touchX = startX;
                    touchY = startY;

                    moveX = 0;
                    moveY = 0;

                    distX = 0;
                    distY = 0;

                    handled |= onPress(view, touchX, touchY);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (pressed) {
                    handled |= true;

                    moveX = event.getRawX() - touchX;
                    moveY = event.getRawY() - touchY;

                    distX += moveX;
                    distY += moveY;

                    touchX = event.getRawX();
                    touchY = event.getRawY();

                    if (distX >= minMovement || distX <= -minMovement || distY >= minMovement || distY <= -minMovement) {
                        if (!moved)
                            onStartMoving(view, touchX, touchY);
                        moved = true;
                    } else if (fixed) {
                        if (moved)
                            onCancelDrag(view, touchX, touchY);
                        moved = false;
                    }

                    if (moved)
                        onDragBy(view, moveX, moveY, distX, distY);
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                touchX = event.getRawX();
                touchY = event.getRawY();

                if (pressed) {
                    handled |= onRelease(view, touchX, touchY, distX, distY);

                    if (moved)
                        onStopMoving(view, touchX, touchY, distX, distY);
                    else
                        onClick(view, touchX, touchY);
                }

                pressed = false;
                moved = false;

                startX = 0;
                startY = 0;

                touchX = 0;
                touchY = 0;

                moveX = 0;
                moveY = 0;

                distX = 0;
                distY = 0;

                break;
        }

        return handled;
    }

    public boolean onPress(View view, float x, float y) {
        return false;
    }

    public boolean onRelease(View view, float x, float y, float distX, float distY) {
        return false;
    }

    public void onStartMoving(View view, float x, float y) {
    }

    public void onStopMoving(View view, float x, float y, float distX, float distY) {
    }

    public void onClick(View view, float x, float y) {
    }

    public void onCancelDrag(View view, float x, float y) {
    }

    public void onDragBy(View view, float moveByX, float moveByY, float distX, float distY) {
    }
}
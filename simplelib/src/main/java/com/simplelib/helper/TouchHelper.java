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

    protected boolean isPressed() {
        return pressed;
    }

    protected boolean isMoved() {
        return moved;
    }

    protected float getStartX() {
        return startX;
    }

    protected float getStartY() {
        return startY;
    }

    protected float getTouchX() {
        return touchX;
    }

    protected float getTouchY() {
        return touchY;
    }

    protected float getDistX() {
        return distX;
    }

    protected float getDistY() {
        return distY;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (!enabled) return false;

        boolean handled = false;

        handled |= super.onTouch(view, event);

        int action = event.getAction();
        switch (action) {
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
                    boolean canceled = action == MotionEvent.ACTION_CANCEL;

                    if (canceled)
                        handled |= onCancel(view);

                    handled |= onRelease(view, canceled, touchX, touchY, distX, distY);

                    if (moved)
                        onStopMoving(view, canceled, touchX, touchY, distX, distY);
                    else
                        onClick(view, canceled, touchX, touchY);
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

    protected boolean onPress(View view, float x, float y) {
        return false;
    }

    protected boolean onRelease(View view, boolean canceled, float x, float y, float distX, float distY) {
        return false;
    }

    protected boolean onCancel(View view) {
        return false;
    }

    protected void onStartMoving(View view, float x, float y) {
    }

    protected void onStopMoving(View view, boolean canceled, float x, float y, float distX, float distY) {
    }

    protected void onClick(View view, boolean canceled, float x, float y) {
    }

    protected void onCancelDrag(View view, float x, float y) {
    }

    protected void onDragBy(View view, float moveByX, float moveByY, float distX, float distY) {
    }
}
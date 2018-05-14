package com.simplelib.helper;

import android.view.MotionEvent;
import android.view.View;

import com.simplelib.tools.Tools;

public class TouchHelper implements View.OnTouchListener {
    private static final int DEFAULT_MAX_MOVEMENT = 5;

    private boolean pressed;
    private boolean moved;

    private float touchX, touchY;
    private float moveX, moveY;

    private int maxMovement = DEFAULT_MAX_MOVEMENT;

    public TouchHelper() {
    }

    public TouchHelper setMaxMovement(int maxMovement) {
        this.maxMovement = maxMovement;
        return this;
    }

    public View applyTo(View view) {
        view.setOnTouchListener(this);
        return view;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int maxMovementInDp = Tools.dpToPx(maxMovement);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!pressed) {
                    pressed = true;
                    onPress(view);
                }

                touchX = event.getX();
                touchY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                moveX = event.getX() - touchX;
                moveY = event.getY() - touchY;

                touchX = event.getX();
                touchY = event.getY();

                if (moveX >= maxMovementInDp || moveX <= -maxMovementInDp || moveY >= maxMovementInDp || moveY <= -maxMovementInDp) {
                    if (!moved)
                        onStartMoving(view);
                    moved = true;
                }

                if (moved)
                    onDragBy(view, moveX, moveY);
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (pressed) {
                    pressed = false;
                    onRelease(view);

                    if (moved)
                        onStopMoving(view);
                    else
                        onClick(view, event.getX(), event.getY());
                    moved = false;
                }
                break;
        }
        return true;
    }

    public void onPress(View view) {
    }

    public void onRelease(View view) {
    }

    public void onStartMoving(View view) {
    }

    public void onStopMoving(View view) {
    }

    public void onClick(View view, float x, float y) {
    }

    public void onDragBy(View view, float x, float y) {
    }
}

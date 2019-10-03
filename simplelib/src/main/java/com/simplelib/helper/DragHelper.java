package com.simplelib.helper;

import android.view.MotionEvent;
import android.view.View;

import com.simplelib.tools.Tools;

public class DragHelper implements View.OnTouchListener {
    public static final int FLAG_MOVE_IN_X = 2;
    public static final int FLAG_MOVE_IN_Y = 4;

    public static final int FLAG_MOVE_TL_BR = 8;
    public static final int FLAG_MOVE_TR_BL = 16;

    private static final int DEFAULT_MAX_MOVEMENT = 5;

    private int directions;

    private boolean moved;
    private float touchX, touchY;
    private float moveX, moveY;

    private int maxMovement = DEFAULT_MAX_MOVEMENT;

    public DragHelper() {
        this.directions = FLAG_MOVE_IN_X | FLAG_MOVE_IN_Y;
    }

    public DragHelper(int directions) {
        this.directions = directions;
    }

    public DragHelper setDirections(int directions) {
        this.directions = directions;
        return this;
    }

    public DragHelper setMaxMovement(int maxMovement) {
        this.maxMovement = maxMovement;
        return this;
    }

    public View applyTo(View view) {
        view.setOnTouchListener(this);
        return view;
    }

    private boolean allowDragIn(int direction) {
        return (directions & direction) == direction;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int maxMovementInDp = Tools.dpToPx(maxMovement);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchX = event.getX();
                touchY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                moveX = event.getX() - touchX;
                moveY = event.getY() - touchY;

                touchX = event.getX();
                touchY = event.getY();

                if (!allowDragIn(FLAG_MOVE_IN_X) && !allowDragIn(FLAG_MOVE_IN_Y)) {
                    if (allowDragIn(FLAG_MOVE_TL_BR)) {
                        float move = 0;
                        if (moveX >= 0 && moveY >= 0)
                            move = Math.max(moveX, moveY);
                        else if (moveX < 0 && moveY < 0)
                            move = Math.min(moveX, moveY);
                        moveX = moveY = move;
                    } else if (allowDragIn(FLAG_MOVE_TR_BL)) {
                        float move = 0;
                        if (moveX >= 0 && moveY <= 0)
                            move = Math.max(moveX, -moveY);
                        else if (moveX < 0 && moveY > 0)
                            move = Math.max(moveX, -moveY);
                        moveX = move;
                        moveY = -move;
                    }
                } else {
                    if (!allowDragIn(FLAG_MOVE_IN_X)) moveX = 0;
                    if (!allowDragIn(FLAG_MOVE_IN_Y)) moveY = 0;
                }

                if (moveX >= maxMovementInDp || moveX <= -maxMovementInDp || moveY >= maxMovementInDp || moveY <= -maxMovementInDp) {
                    if (!moved)
                        onStartMoving();
                    moved = true;
                }
                if (moved) {
                    v.setX(v.getX() + moveX);
                    v.setY(v.getY() + moveY);

                    onDragBy(moveX, moveY);
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (moved)
                    onStopMoving();
                else
                    onClick(event.getX(), event.getY());
                moved = false;
                break;
        }
        return true;
    }

    protected void onStartMoving() {
    }

    protected void onStopMoving() {
    }

    protected void onClick(float x, float y) {
    }

    protected void onDragBy(float x, float y) {
    }
}

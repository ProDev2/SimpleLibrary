package com.simplelib.helper;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class GestureHelper extends TouchHelper implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    private GestureDetector gestureDetector;

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        boolean handled = false;

        if (gestureDetector == null)
            gestureDetector = new GestureDetector(view.getContext(), this);
        handled |= gestureDetector.onTouchEvent(event);

        handled |= super.onTouch(view, event);

        return handled;
    }

    @Override
    public boolean onSingleTapUp(android.view.MotionEvent e) {
        return false;
    }

    @Override
    public void onLongPress(android.view.MotionEvent e) {
    }

    @Override
    public boolean onScroll(android.view.MotionEvent e1, android.view.MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public boolean onFling(android.view.MotionEvent e1, android.view.MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void onShowPress(android.view.MotionEvent e) {
    }

    @Override
    public boolean onDown(android.view.MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(android.view.MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(android.view.MotionEvent e) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(android.view.MotionEvent e) {
        return false;
    }
}
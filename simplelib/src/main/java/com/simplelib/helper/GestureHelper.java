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
    public boolean onSingleTapUp(MotionEvent event) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent event) {
    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent event) {
    }

    @Override
    public boolean onDown(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        return false;
    }
}
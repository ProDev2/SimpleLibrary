package com.simplelib.helper;

import android.view.MotionEvent;
import android.view.View;

public class BasicTouchHelper implements View.OnTouchListener {
    private View.OnTouchListener innerTouchListener;

    public BasicTouchHelper() {
    }

    public View applyTo(View view) {
        view.setOnTouchListener(this);
        return view;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        boolean handled = false;

        if (innerTouchListener != null)
            handled |= innerTouchListener.onTouch(view, event);

        return handled;
    }

    public void setInnerTouchListener(View.OnTouchListener innerTouchListener) {
        this.innerTouchListener = innerTouchListener;
    }
}

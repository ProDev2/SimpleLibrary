package com.simplelib.animator;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.support.v4.view.animation.FastOutSlowInInterpolator;

public class SimpleColorAnimator implements ValueAnimator.AnimatorUpdateListener {
    private ValueAnimator animator;
    private int duration;

    private int value;

    private float[] from;
    private float[] to;
    private float[] hsv;

    private Listener listener;

    private Animator.AnimatorListener animListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            try {
                listener.finish();
            } catch (Exception e) {
            }
        }

        @Override
        public void onAnimationCancel(Animator animator) {
            try {
                listener.finish();
            } catch (Exception e) {
            }
        }

        @Override
        public void onAnimationRepeat(Animator animator) {
        }
    };

    public SimpleColorAnimator(int duration) {
        this.duration = duration;
        this.value = 0xff000000;
    }

    public SimpleColorAnimator(int duration, int startColor) {
        this.duration = duration;
        this.value = startColor;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isAnimating() {
        try {
            if (animator != null)
                return animator.isRunning();
        } catch (Exception e) {
        }

        return false;
    }

    public void animateTo(int color) {
        if (this.value == color) return;

        abort();

        from = new float[3];
        to = new float[3];
        hsv = new float[3];

        Color.colorToHSV(value, from);
        Color.colorToHSV(color, to);

        animator = ValueAnimator.ofFloat(0, 1);
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.addListener(animListener);
        animator.addUpdateListener(this);
        animator.setDuration(duration);
        animator.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animator) {
        hsv[0] = from[0] + (to[0] - from[0]) * animator.getAnimatedFraction();
        hsv[1] = from[1] + (to[1] - from[1]) * animator.getAnimatedFraction();
        hsv[2] = from[2] + (to[2] - from[2]) * animator.getAnimatedFraction();

        value = Color.HSVToColor(hsv);

        try {
            listener.update(value);
        } catch (Exception e) {
        }
    }

    private void abort() {
        try {
            if (animator != null)
                animator.cancel();
        } catch (Exception e) {
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public static interface Listener {
        void update(int color);

        void finish();
    }
}

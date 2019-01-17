package com.simplelib.animator;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.animation.FastOutSlowInInterpolator;

public class SimpleColorAnimator implements ValueAnimator.AnimatorUpdateListener {
    private ValueAnimator animator;
    private int duration;

    private int value;

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

        try {
            abort();

            animator = ValueAnimator.ofObject(new ArgbEvaluator(), value, color);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                animator = ValueAnimator.ofArgb(value, color);
            }

            animator.setInterpolator(new FastOutSlowInInterpolator());
            animator.addListener(animListener);
            animator.addUpdateListener(this);
            animator.setDuration(duration);
            animator.start();
        } catch (Exception e) {
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animator) {
        try {
            value = (int) animator.getAnimatedValue();
        } catch (Exception e) {
        }

        try {
            listener.update(value);
        } catch (Exception e) {
        }
    }

    public void abort() {
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

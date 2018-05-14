package com.simplelib.animator;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;

public class SimpleValueAnimator implements ValueAnimator.AnimatorUpdateListener {
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

    public SimpleValueAnimator(int duration) {
        this.duration = duration;
    }

    public SimpleValueAnimator(int duration, int startValue) {
        this.duration = duration;
        this.value = startValue;
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

    public void animateTo(int value) {
        if (this.value == value) return;

        abort();

        animator = ValueAnimator.ofInt(this.value, value);
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.addListener(animListener);
        animator.addUpdateListener(this);
        animator.setDuration(duration);
        animator.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animator) {
        value = (int) animator.getAnimatedValue();

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
        void update(int value);

        void finish();
    }
}

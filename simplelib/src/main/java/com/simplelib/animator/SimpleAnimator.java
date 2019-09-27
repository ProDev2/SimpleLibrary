package com.simplelib.animator;

import android.animation.Animator;
import android.animation.ValueAnimator;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

public class SimpleAnimator implements ValueAnimator.AnimatorUpdateListener {
    private ValueAnimator animator;
    private int duration;

    private int start;
    private int end;

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

    public SimpleAnimator(int duration, int start, int end) {
        this.duration = duration;
        this.start = start;
        this.end = end;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getDelta() {
        return Math.max(start, end) - Math.min(start, end);
    }

    public boolean isAnimating() {
        try {
            if (animator != null)
                return animator.isRunning();
        } catch (Exception e) {
        }

        return false;
    }

    public void animateToEnd() {
        abort();

        animator = ValueAnimator.ofInt(value != 0 ? value : start, end);
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.addListener(animListener);
        animator.addUpdateListener(this);
        animator.setDuration(duration);
        animator.start();
    }

    public void animateToStart() {
        abort();

        animator = ValueAnimator.ofInt(value != 0 ? value : end, start);
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
            listener.update((int) animator.getAnimatedValue());
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

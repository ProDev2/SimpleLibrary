package com.simplelib.insets.window;

import android.graphics.Rect;
import android.os.Build;
import android.view.DisplayCutout;
import android.view.WindowInsets;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.simplelib.insets.Insets;
import com.simplelib.insets.RectInsets;

public class WinInsets extends RectInsets {
    @NonNull
    public static final WinInsets EMPTY;

    static {
        EMPTY = new WinInsets(false);
        EMPTY.consume();
        EMPTY.setStable(true);
    }

    @NonNull
    public static final WinInsets copy(@Nullable WinInsets srcInsets) {
        WinInsets insets = null;
        if (srcInsets != null)
            insets = (WinInsets) srcInsets.copy();
        else if (EMPTY != null)
            insets = copy(EMPTY);

        if (insets == null)
            throw new IllegalStateException("Something went wrong");

        insets.setStable(true);
        return insets;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @NonNull
    public static final WinInsets with(@Nullable WindowInsets srcInsets) {
        WinInsets insets = null;
        if (srcInsets != null)
            insets = new WinInsets(srcInsets);
        else if (EMPTY != null)
            insets = copy(EMPTY);

        if (insets == null)
            throw new IllegalStateException("Something went wrong");

        insets.setStable(true);
        return insets;
    }

    // Window insets
    private boolean round;

    public WinInsets(boolean round) {
        super(3);

        this.round = round;
    }

    public WinInsets(@NonNull WinInsets srcInsets) {
        super(srcInsets);

        this.round = srcInsets.round;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    public WinInsets(WindowInsets srcInsets) {
        this(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH
                && srcInsets.isRound());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                srcInsets.isConsumed()) {
            consume();
            return;
        }

        try {
            if (srcInsets.hasSystemWindowInsets()) {
                setSystemWindowInsetsLeft(srcInsets.getSystemWindowInsetLeft());
                setSystemWindowInsetsTop(srcInsets.getSystemWindowInsetTop());
                setSystemWindowInsetsRight(srcInsets.getSystemWindowInsetRight());
                setSystemWindowInsetsBottom(srcInsets.getSystemWindowInsetBottom());
            } else {
                consumeSystemWindowInsets();
            }
        } catch (Exception e) {
            consumeSystemWindowInsets();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                if (srcInsets.hasStableInsets()) {
                    setStableInsetsLeft(srcInsets.getStableInsetLeft());
                    setStableInsetsTop(srcInsets.getStableInsetTop());
                    setStableInsetsRight(srcInsets.getStableInsetRight());
                    setStableInsetsBottom(srcInsets.getStableInsetBottom());
                } else {
                    consumeStableInsets();
                }
            } catch (Exception e) {
                consumeStableInsets();
            }
        } else {
            consumeStableInsets();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                DisplayCutout displayCutout = srcInsets.getDisplayCutout();
                if (displayCutout != null) {
                    setDisplayCutoutLeft(displayCutout.getSafeInsetLeft());
                    setDisplayCutoutTop(displayCutout.getSafeInsetTop());
                    setDisplayCutoutRight(displayCutout.getSafeInsetRight());
                    setDisplayCutoutBottom(displayCutout.getSafeInsetBottom());
                } else {
                    consumeDisplayCutout();
                }
            } catch (Exception e) {
                consumeDisplayCutout();
            }
        } else {
            consumeDisplayCutout();
        }
    }

    @NonNull
    @Override
    public Insets copy() {
        return new WinInsets(this);
    }

    public boolean isRound() {
        return round;
    }

    void setRound(boolean round) {
        throwIfStable();
        this.round = round;
    }

    // Get flags
    public int getSystemWindowInsetsFlags() {
        return super.getFlags(0);
    }

    public int getStableInsetsFlags() {
        return super.getFlags(1);
    }

    public int getDisplayCutoutFlags() {
        return super.getFlags(2);
    }

    // Set flags
    public void setSystemWindowInsetsFlags(int flags) {
        super.setFlags(0, flags);
    }

    public void setStableInsetsFlags(int flags) {
        super.setFlags(1, flags);
    }

    public void setDisplayCutoutFlags(int flags) {
        super.setFlags(2, flags);
    }

    // Get system window insets
    @NonNull
    public Rect getSystemWindowInsets() {
        return super.getInsets(0);
    }

    public int getSystemWindowInsetsLeft() {
        return super.getInsetsLeft(0);
    }

    public int getSystemWindowInsetsTop() {
        return super.getInsetsTop(0);
    }

    public int getSystemWindowInsetsRight() {
        return super.getInsetsRight(0);
    }

    public int getSystemWindowInsetsBottom() {
        return super.getInsetsBottom(0);
    }

    // Get stable insets
    @NonNull
    public Rect getStableInsets() {
        return super.getInsets(1);
    }

    public int getStableInsetsLeft() {
        return super.getInsetsLeft(1);
    }

    public int getStableInsetsTop() {
        return super.getInsetsTop(1);
    }

    public int getStableInsetsRight() {
        return super.getInsetsRight(1);
    }

    public int getStableInsetsBottom() {
        return super.getInsetsBottom(1);
    }

    // Get display cutout
    @NonNull
    public Rect getDisplayCutout() {
        return super.getInsets(2);
    }

    public int getDisplayCutoutLeft() {
        return super.getInsetsLeft(2);
    }

    public int getDisplayCutoutTop() {
        return super.getInsetsTop(2);
    }

    public int getDisplayCutoutRight() {
        return super.getInsetsRight(2);
    }

    public int getDisplayCutoutBottom() {
        return super.getInsetsBottom(2);
    }

    // Set system window insets
    public void setSystemWindowInsets(@NonNull Rect insets) {
        super.setInsets(0, insets);
    }

    public void setSystemWindowInsetsLeft(int left) {
        super.setInsetsLeft(0, left);
    }

    public void setSystemWindowInsetsTop(int top) {
        super.setInsetsTop(0, top);
    }

    public void setSystemWindowInsetsRight(int right) {
        super.setInsetsRight(0, right);
    }

    public void setSystemWindowInsetsBottom(int bottom) {
        super.setInsetsBottom(0, bottom);
    }

    // Set stable insets
    public void setStableInsets(@NonNull Rect insets) {
        super.setInsets(1, insets);
    }

    public void setStableInsetsLeft(int left) {
        super.setInsetsLeft(1, left);
    }

    public void setStableInsetsTop(int top) {
        super.setInsetsTop(1, top);
    }

    public void setStableInsetsRight(int right) {
        super.setInsetsRight(1, right);
    }

    public void setStableInsetsBottom(int bottom) {
        super.setInsetsBottom(1, bottom);
    }

    // Set display cutout
    public void setDisplayCutout(@NonNull Rect insets) {
        super.setInsets(2, insets);
    }

    public void setDisplayCutoutLeft(int left) {
        super.setInsetsLeft(2, left);
    }

    public void setDisplayCutoutTop(int top) {
        super.setInsetsTop(2, top);
    }

    public void setDisplayCutoutRight(int right) {
        super.setInsetsRight(2, right);
    }

    public void setDisplayCutoutBottom(int bottom) {
        super.setInsetsBottom(2, bottom);
    }

    // Are system window insets consumed
    public boolean isSystemWindowInsetsConsumed() {
        return super.isConsumed(0);
    }

    public boolean isSystemWindowInsetsLeftConsumed() {
        return super.isLeftConsumed(0);
    }

    public boolean isSystemWindowInsetsTopConsumed() {
        return super.isTopConsumed(0);
    }

    public boolean isSystemWindowInsetsRightConsumed() {
        return super.isRightConsumed(0);
    }

    public boolean isSystemWindowInsetsBottomConsumed() {
        return super.isBottomConsumed(0);
    }

    // Are stable insets consumed
    public boolean isStableInsetsConsumed() {
        return super.isConsumed(1);
    }

    public boolean isStableInsetsLeftConsumed() {
        return super.isLeftConsumed(1);
    }

    public boolean isStableInsetsTopConsumed() {
        return super.isTopConsumed(1);
    }

    public boolean isStableInsetsRightConsumed() {
        return super.isRightConsumed(1);
    }

    public boolean isStableInsetsBottomConsumed() {
        return super.isBottomConsumed(1);
    }

    // Is display cutout consumed
    public boolean isDisplayCutoutConsumed() {
        return super.isConsumed(2);
    }

    public boolean isDisplayCutoutLeftConsumed() {
        return super.isLeftConsumed(2);
    }

    public boolean isDisplayCutoutTopConsumed() {
        return super.isTopConsumed(2);
    }

    public boolean isDisplayCutoutRightConsumed() {
        return super.isRightConsumed(2);
    }

    public boolean isDisplayCutoutBottomConsumed() {
        return super.isBottomConsumed(2);
    }

    // Consume system window insets
    public void consumeSystemWindowInsets() {
        super.consume(0);
    }

    public void consumeSystemWindowInsetsLeft() {
        super.consumeLeft(0);
    }

    public void consumeSystemWindowInsetsTop() {
        super.consumeTop(0);
    }

    public void consumeSystemWindowInsetsRight() {
        super.consumeRight(0);
    }

    public void consumeSystemWindowInsetsBottom() {
        super.consumeBottom(0);
    }

    // Consume stable insets
    public void consumeStableInsets() {
        super.consume(1);
    }

    public void consumeStableInsetsLeft() {
        super.consumeLeft(1);
    }

    public void consumeStableInsetsTop() {
        super.consumeTop(1);
    }

    public void consumeStableInsetsRight() {
        super.consumeRight(1);
    }

    public void consumeStableInsetsBottom() {
        super.consumeBottom(1);
    }

    // Consume display cutout
    public void consumeDisplayCutout() {
        super.consume(2);
    }

    public void consumeDisplayCutoutLeft() {
        super.consumeLeft(2);
    }

    public void consumeDisplayCutoutTop() {
        super.consumeTop(2);
    }

    public void consumeDisplayCutoutRight() {
        super.consumeRight(2);
    }

    public void consumeDisplayCutoutBottom() {
        super.consumeBottom(2);
    }
}

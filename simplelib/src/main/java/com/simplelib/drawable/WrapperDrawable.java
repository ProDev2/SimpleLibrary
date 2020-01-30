package com.simplelib.drawable;

import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;
import androidx.appcompat.graphics.drawable.DrawableWrapper;

class WrapperDrawable extends DrawableWrapper {
    WrapperDrawable(Drawable drawable) {
        super(drawable);
    }

    @Nullable
    protected Drawable getDrawable() {
        return getWrappedDrawable();
    }

    protected void setDrawable(@Nullable Drawable drawable) {
        setWrappedDrawable(drawable);
    }
}

package com.simplelib.holder;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.simplelib.tools.UIUtils;

public final class ColorHolder {
    // Initialization
    @NonNull
    public static ColorHolder create() {
        return new ColorHolder();
    }

    @NonNull
    public static ColorHolder withColor(@ColorInt int color) {
        final ColorHolder colorHolder = new ColorHolder();
        colorHolder.color = color;
        return colorHolder;
    }

    @NonNull
    public static ColorHolder withColorRes(@ColorRes int colorRes) {
        final ColorHolder colorHolder = new ColorHolder();
        colorHolder.colorRes = colorRes;
        return colorHolder;
    }

    // Holder
    public @Nullable @ColorInt Integer color;
    public @Nullable @ColorRes Integer colorRes;

    public ColorHolder() {
    }

    public boolean hasColor() {
        return this.color != null ||
                this.colorRes != null;
    }

    public ColorHolder clear() {
        this.color = null;
        this.colorRes = null;
        return this;
    }

    public void setColor(@Nullable @ColorInt Integer color) {
        this.color = color;
    }

    public void setColorRes(@Nullable @ColorRes Integer colorRes) {
        this.colorRes = colorRes;
    }

    /**
     * a small helper to get the color from the colorHolder
     *
     * @param ctx
     * @return
     */
    public int getColorInt(Context ctx) {
        final Integer color = getColor(ctx);
        return color != null ? color : 0;
    }

    /**
     * a small helper to get the color from the colorHolder
     *
     * @param ctx
     * @param defColor
     * @return
     */
    public int getColor(Context ctx, @ColorInt int defColor) {
        final Integer color = getColor(ctx);
        return color != null ? color : defColor;
    }

    /**
     * a small helper to get the color from the colorHolder
     *
     * @param ctx
     * @return
     */
    @Nullable
    public Integer getColor(Context ctx) {
        if (color == null && colorRes != null && ctx != null) {
            try {
                color = ContextCompat.getColor(ctx, colorRes);
            } catch (Exception e) {
            }
        }
        return color;
    }

    /**
     * a small helper to get the color from the colorHolder
     *
     * @param ctx
     * @param attr
     * @param defColor
     * @return
     */
    public int getColor(Context ctx, @AttrRes int attr, @ColorInt int defColor) {
        final Integer color = getColor(ctx);
        try {
            return color != null ? color : UIUtils.getColor(ctx, attr, defColor);
        } catch (Exception e) {
            return defColor;
        } catch (Throwable tr) {
            tr.printStackTrace();

            return defColor;
        }
    }

    /**
     * set the textColor of the ColorHolder to an drawable
     *
     * @param ctx
     * @param drawable
     */
    public void applyTo(Context ctx, GradientDrawable drawable) {
        if (drawable == null)
            return;

        final Integer color = getColor(ctx);
        if (color != null) {
            drawable.setColor(color);
        }
    }


    /**
     * set the textColor of the ColorHolder to a view
     *
     * @param view
     */
    public void applyToBackground(View view) {
        if (view == null)
            return;

        if (color != null) {
            view.setBackgroundColor(color);
        } else if (colorRes != null) {
            view.setBackgroundResource(colorRes);
        }
    }

    /**
     * a small helper to set the text color to a textView null save
     *
     * @param textView
     */
    public void applyTo(TextView textView) {
        if (textView == null)
            return;

        final Integer color = getColor(textView.getContext());
        if (color != null) {
            textView.setTextColor(color);
        }
    }

    /**
     * a small helper to set the text color to a textView null save
     *
     * @param textView
     * @param colorDefault
     */
    public void applyToOr(TextView textView, ColorStateList colorDefault) {
        if (textView == null)
            return;

        final Integer color = getColor(textView.getContext());
        if (color != null) {
            textView.setTextColor(color);
        } else if (colorDefault != null) {
            textView.setTextColor(colorDefault);
        }
    }

    /**
     * a small static helper class to get the color from the colorHolder
     *
     * @param colorHolder
     * @param ctx
     * @return
     */
    public static int getColorInt(ColorHolder colorHolder, Context ctx) {
        return colorHolder != null ? colorHolder.getColorInt(ctx) : 0;
    }

    /**
     * a small static helper class to get the color from the colorHolder
     *
     * @param colorHolder
     * @param ctx
     * @return
     */
    public static int getColor(ColorHolder colorHolder, Context ctx, @ColorInt int defColor) {
        return colorHolder != null ? colorHolder.getColor(ctx, defColor) : defColor;
    }

    /**
     * a small static helper class to get the color from the colorHolder
     *
     * @param colorHolder
     * @param ctx
     * @return
     */
    @Nullable
    public static Integer getColor(ColorHolder colorHolder, Context ctx) {
        return colorHolder != null ? colorHolder.getColor(ctx) : null;
    }

    /**
     * a small static helper to set the text color to a textView null save
     *
     * @param colorHolder
     * @param textView
     */
    public static void applyTo(ColorHolder colorHolder, TextView textView) {
        if (colorHolder != null && textView != null) {
            colorHolder.applyTo(textView);
        }
    }

    /**
     * a small static helper to set the text color to a textView null save
     *
     * @param colorHolder
     * @param textView
     * @param colorDefault
     */
    public static void applyToOr(ColorHolder colorHolder, TextView textView, ColorStateList colorDefault) {
        if (colorHolder != null && textView != null) {
            colorHolder.applyToOr(textView, colorDefault);
        } else if (textView != null && colorDefault != null) {
            textView.setTextColor(colorDefault);
        }
    }

    /**
     * a small static helper to set the color to a GradientDrawable null save
     *
     * @param colorHolder
     * @param ctx
     * @param gradientDrawable
     */
    public static void applyToOrTransparent(ColorHolder colorHolder, Context ctx, GradientDrawable gradientDrawable) {
        if (colorHolder != null && gradientDrawable != null) {
            colorHolder.applyTo(ctx, gradientDrawable);
        } else if (gradientDrawable != null) {
            gradientDrawable.setColor(Color.TRANSPARENT);
        }
    }
}

/*
 * Copyright (c) 2020 ProDev+ (Pascal Gerner).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.simplelib.tools;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.annotation.StyleableRes;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.simplelib.R;

public final class UIUtils {
    // UI Values
    public static final int NO_RESOURCE_ID;
    public static final int NO_VIEW_ID;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            NO_RESOURCE_ID = Resources.ID_NULL;
        } else {
            NO_RESOURCE_ID = 0;
        }

        NO_VIEW_ID = View.NO_ID;
    }

    // Constructor
    private UIUtils() {
    }

    // Appearance
    public static void setWindowFlag(@NonNull Activity activity, final int bits, boolean enabled) {
        if (activity == null)
            return;

        setWindowFlag(activity.getWindow(), bits, enabled);
    }

    public static void setWindowFlag(@NonNull Window window, final int bits, boolean enabled) {
        if (window == null)
            return;

        WindowManager.LayoutParams winParams = window.getAttributes();
        if (enabled)
            winParams.flags |= bits;
        else
            winParams.flags &= ~bits;
        window.setAttributes(winParams);
    }

    public static void setSystemUiVisibilityFlag(@NonNull Activity activity, final int bits, boolean enabled) {
        if (activity == null)
            return;

        setSystemUiVisibilityFlag(activity.getWindow(), bits, enabled);
    }

    public static void setSystemUiVisibilityFlag(@NonNull Window window, final int bits, boolean enabled) {
        View view = window != null ? window.getDecorView() : null;
        if (view == null) return;

        int flags = view.getSystemUiVisibility();
        if (enabled)
            flags |= bits;
        else
            flags &= ~bits;
        view.setSystemUiVisibility(flags);
    }

    // Theme
    public static TypedArray obtain(@NonNull Resources.Theme theme, @NonNull @StyleableRes int[] attrs) {
        return obtain(theme, null, attrs, 0, 0);
    }

    public static TypedArray obtain(@NonNull Resources.Theme theme, @StyleRes int resId, @NonNull @StyleableRes int[] attrs) {
        return obtain(theme, null, attrs, 0, resId);
    }

    public static TypedArray obtain(@NonNull Resources.Theme theme, @NonNull @StyleableRes int[] attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        return obtain(theme, null, attrs, defStyleAttr, defStyleRes);
    }

    public static TypedArray obtain(@NonNull Resources.Theme theme, @Nullable AttributeSet set, @NonNull @StyleableRes int[] attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        if (theme == null)
            return null;

        try {
            return theme.obtainStyledAttributes(set, attrs, defStyleAttr, defStyleRes);
        } catch (Exception ignored) {
        } catch (Throwable tr) {
            tr.printStackTrace();
        }

        return null;
    }

    // Colors
    public static @ColorInt int getColor(@NonNull Context context, @AttrRes int attr) {
        return getColor(context, attr, Color.TRANSPARENT);
    }

    public static @ColorInt int getColor(@NonNull Context context, @AttrRes int attr, @ColorInt int defColor) {
        return context != null ? getColor(context.getTheme(), attr, defColor) : defColor;
    }

    public static @ColorInt int getColor(@NonNull Resources.Theme theme, @AttrRes int attr) {
        return getColor(theme, attr, Color.TRANSPARENT);
    }

    public static @ColorInt int getColor(@NonNull Resources.Theme theme, @AttrRes int attr, @ColorInt int defColor) {
        if (theme == null)
            throw new NullPointerException("No theme attached");

        try {
            TypedValue value = new TypedValue();
            boolean success = theme.resolveAttribute(attr, value, true);
            return success ? value.data : defColor;
        } catch (Exception e) {
            return defColor;
        } catch (Throwable tr) {
            tr.printStackTrace();

            return defColor;
        }
    }

    public static @NonNull @ColorInt int[] getColors(@NonNull Context context, @NonNull @AttrRes int[] attrs) {
        return getColors(context, attrs, (int[]) null);
    }

    public static @NonNull @ColorInt int[] getColors(@NonNull Context context, @NonNull @AttrRes int[] attrs, @ColorInt int defColor) {
        return getColors(context, attrs, new int[] {defColor});
    }

    public static @NonNull @ColorInt int[] getColors(@NonNull Context context, @NonNull @AttrRes int[] attrs, @Nullable @ColorInt int[] defColors) {
        return context != null ? getColors(context.getTheme(), attrs, defColors) : defColors;
    }

    public static @NonNull @ColorInt int[] getColors(@NonNull Resources.Theme theme, @NonNull @AttrRes int[] attrs) {
        return getColors(theme, attrs, (int[]) null);
    }

    public static @NonNull @ColorInt int[] getColors(@NonNull Resources.Theme theme, @NonNull @AttrRes int[] attrs, @ColorInt int defColor) {
        return getColors(theme, attrs, new int[] {defColor});
    }

    public static @NonNull @ColorInt int[] getColors(@NonNull Resources.Theme theme, @NonNull @AttrRes int[] attrs, @Nullable @ColorInt int[] defColors) {
        if (attrs == null)
            throw new NullPointerException("No attributes attached");

        final int amount = attrs.length;
        final int[] results = new int[amount];

        final int defAmount;
        if (defColors != null && (defAmount = defColors.length) > 0) {
            int defColor = Color.TRANSPARENT;
            for (int index = 0; index < amount; index++) {
                if (index < defAmount)
                    defColor = defColors[index];
                results[index] = defColor;
            }
        }

        final TypedValue value = new TypedValue();
        for (int index = 0; index < amount; index++) {
            try {
                boolean success = theme.resolveAttribute(attrs[index], value, true);
                if (success)
                    results[index] = value.data;
            } catch (Exception ignored) {
            } catch (Throwable tr) {
                tr.printStackTrace();
                break;
            }
        }

        return results;
    }

    // Theme attributes
    public static int getResId(@NonNull Context context, @AttrRes int attrId) {
        if (context == null)
            return NO_RESOURCE_ID;

        return getResId(context.getTheme(), attrId);
    }

    public static int getResId(@NonNull Resources.Theme theme, @AttrRes int attrId) {
        if (theme == null)
            return NO_RESOURCE_ID;

        try {
            final TypedValue value = new TypedValue();
            theme.resolveAttribute(attrId, value, true);
            return value.resourceId;
        } catch (Exception e) {
            return NO_RESOURCE_ID;
        } catch (Throwable tr) {
            tr.printStackTrace();

            return NO_RESOURCE_ID;
        }
    }

    // Drawable
    @Nullable
    public static Drawable getDrawable(@NonNull Context context, @DrawableRes int resId) {
        if (context == null)
            return null;

        try {
            return ContextCompat.getDrawable(context, resId);
        } catch (Exception e) {
            return null;
        } catch (Throwable tr) {
            tr.printStackTrace();

            return null;
        }
    }

    // Background
    public static int getSelectableBackgroundRes(@NonNull Context context) {
        return getResId(context, R.attr.selectableItemBackground);
    }

    @Nullable
    public static Drawable getSelectableBackground(@NonNull Context context) {
        int selectableBackgroundRes = getSelectableBackgroundRes(context);
        return getDrawable(context, selectableBackgroundRes);
    }

    // Views
    public static boolean isLayoutRtl(@NonNull View view) {
        try {
            if (view != null)
                return ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_RTL;
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean isLayoutRtl() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Configuration config = Resources.getSystem().getConfiguration();
                return config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static void applyEffect(@NonNull Context context, @NonNull View view, @ColorInt int selectedColor, @ColorInt int pressedColor) {
        applyEffect(context, view, selectedColor, pressedColor, -1, null, false);
    }

    public static void applyEffect(@NonNull Context context, @NonNull View view, @ColorInt int selectedColor, @ColorInt int pressedColor, boolean legacyStyle) {
        applyEffect(context, view, selectedColor, pressedColor, -1, null, legacyStyle);
    }

    public static void applyEffect(@NonNull Context context, @NonNull View view, @ColorInt int selectedColor, @ColorInt int pressedColor, @Nullable ShapeAppearanceModel shape, boolean legacyStyle) {
        applyEffect(context, view, selectedColor, pressedColor, -1, shape, legacyStyle);
    }

    public static void applyEffect(@NonNull Context context, @NonNull View view, @ColorInt int selectedColor, @ColorInt int pressedColor, int animationDuration, boolean legacyStyle) {
        applyEffect(context, view, selectedColor, pressedColor, animationDuration, null, legacyStyle);
    }

    public static void applyEffect(@NonNull Context context, @NonNull View view, @ColorInt int selectedColor, @ColorInt int pressedColor, int animationDuration, @Nullable ShapeAppearanceModel shape, boolean legacyStyle) {
        if (context == null || view == null)
            return;

        if (shape == null) {
            int cornerRadius = Math.max(context.getResources().getDimensionPixelSize(R.dimen.material_background_corner_radius), 0);
            shape = new ShapeAppearanceModel().withCornerSize((float) cornerRadius);
        }

        final Drawable selected;
        final Drawable unselected;

        if (legacyStyle) {
            selected = new ColorDrawable(selectedColor);
            unselected = getSelectableBackground(context);
        } else {
            int paddingTopBottom = Math.max(context.getResources().getDimensionPixelSize(R.dimen.material_background_padding_top_bottom), 0);
            int paddingStartEnd = Math.max(context.getResources().getDimensionPixelSize(R.dimen.material_background_padding_start_end), 0);

            final MaterialShapeDrawable selectedShapeDrawable = new MaterialShapeDrawable(shape);
            selectedShapeDrawable.setFillColor(ColorStateList.valueOf(selectedColor));
            selected = new InsetDrawable(selectedShapeDrawable, paddingStartEnd, paddingTopBottom, paddingStartEnd, paddingTopBottom);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final MaterialShapeDrawable unselectedMaskShapeDrawable = new MaterialShapeDrawable(shape);
                unselectedMaskShapeDrawable.setFillColor(ColorStateList.valueOf(Color.BLACK));
                final InsetDrawable unselectedMaskDrawable = new InsetDrawable(unselectedMaskShapeDrawable, paddingStartEnd, paddingTopBottom, paddingStartEnd, paddingTopBottom);

                unselected = new RippleDrawable(new ColorStateList(new int[1][0], new int[] {pressedColor}), null, unselectedMaskDrawable);
            } else {
                final MaterialShapeDrawable unselectedShapeDrawable = new MaterialShapeDrawable(shape);
                unselectedShapeDrawable.setFillColor(ColorStateList.valueOf(pressedColor));
                final InsetDrawable unselectedDrawable = new InsetDrawable(unselectedShapeDrawable, paddingStartEnd, paddingTopBottom, paddingStartEnd, paddingTopBottom);

                final StateListDrawable unselectedStateDrawable = new StateListDrawable();

                if (animationDuration > 0L) {
                    unselectedStateDrawable.setEnterFadeDuration(animationDuration);
                    unselectedStateDrawable.setExitFadeDuration(animationDuration);
                }

                unselectedStateDrawable.addState(new int[] {android.R.attr.state_pressed}, unselectedDrawable);
                unselectedStateDrawable.addState(new int[] {}, new ColorDrawable(Color.TRANSPARENT));

                unselected = unselectedStateDrawable;
            }
        }

        final StateListDrawable stateDrawable = new StateListDrawable();

        if (animationDuration > 0L) {
            stateDrawable.setEnterFadeDuration(animationDuration);
            stateDrawable.setExitFadeDuration(animationDuration);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            stateDrawable.addState(new int[] {android.R.attr.state_selected}, selected);
            stateDrawable.addState(new int[] {}, new ColorDrawable(Color.TRANSPARENT));

            ViewCompat.setBackground(view, stateDrawable);
            view.setForeground(unselected);
        } else {
            stateDrawable.addState(new int[] {android.R.attr.state_selected}, selected);
            stateDrawable.addState(new int[] {}, unselected);

            ViewCompat.setBackground(view, stateDrawable);
        }
    }
}

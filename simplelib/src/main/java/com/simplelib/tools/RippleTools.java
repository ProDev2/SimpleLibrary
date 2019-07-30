package com.simplelib.tools;

import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Arrays;

public class RippleTools {
    public static boolean isRippleDrawable(Drawable drawable) {
        if (drawable == null) return false;
        if (isShapeDrawable(drawable))
            return true;
        if (isStateListDrawable(drawable))
            return true;
        return false;
    }

    public static boolean isShapeDrawable(Drawable drawable) {
        if (drawable == null) return false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return drawable instanceof ShapeDrawable &&
                    ((ShapeDrawable) drawable).getShape() instanceof RoundRectShape;
        }
        return false;
    }

    public static boolean isStateListDrawable(Drawable drawable) {
        if (drawable == null) return false;
        return drawable instanceof StateListDrawable &&
                ((StateListDrawable) drawable).getState() != null;
    }

    public static Drawable getAdaptiveRippleDrawable(float rippleRadii, int normalColor, int pressedColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new RippleDrawable(ColorStateList.valueOf(pressedColor), null, getRippleMask(rippleRadii, normalColor));
        } else {
            return getStateListDrawable(normalColor, pressedColor);
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private static Drawable getRippleMask(float rippleRadii, int color) {
        float[] outerRadii = new float[8];
        // 3 is radius of final ripple,
        // instead of 3 you can give required final radius
        Arrays.fill(outerRadii, rippleRadii);

        RoundRectShape r = new RoundRectShape(outerRadii, null, null);
        ShapeDrawable shapeDrawable = new ShapeDrawable(r);
        shapeDrawable.getPaint().setColor(color);
        return shapeDrawable;
    }

    public static StateListDrawable getStateListDrawable(
            int normalColor, int pressedColor) {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed},
                new ColorDrawable(pressedColor));
        states.addState(new int[]{android.R.attr.state_focused},
                new ColorDrawable(pressedColor));
        states.addState(new int[]{android.R.attr.state_activated},
                new ColorDrawable(pressedColor));
        states.addState(new int[]{},
                new ColorDrawable(normalColor));
        return states;
    }
}

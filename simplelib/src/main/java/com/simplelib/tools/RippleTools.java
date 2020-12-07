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

import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Arrays;

public class RippleTools {
    public static boolean isRippleDrawable(Drawable drawable) {
        if (drawable == null) return false;
        if (isShapeDrawable(drawable))
            return true;
        return isStateListDrawable(drawable);
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
                drawable.getState() != null;
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
        states.addState(new int[] {android.R.attr.state_pressed},
                new ColorDrawable(pressedColor));
        states.addState(new int[] {android.R.attr.state_focused},
                new ColorDrawable(pressedColor));
        states.addState(new int[] {android.R.attr.state_activated},
                new ColorDrawable(pressedColor));
        states.addState(new int[] {},
                new ColorDrawable(normalColor));
        return states;
    }
}

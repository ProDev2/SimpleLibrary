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

import android.content.res.Resources;
import android.util.DisplayMetrics;

public class MathTools {
    public static float dpToPx(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    public static int dpToPx(int dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return (int) (dp * (metrics.densityDpi / 160f));
    }

    public static float getDistance(float n1, float n2) {
        return Math.max(n1, n2) - Math.min(n1, n2);
    }

    public static int getDistance(int n1, int n2) {
        return Math.max(n1, n2) - Math.min(n1, n2);
    }

    public static int makePositive(int n) {
        return n >= 0 ? n : -n;
    }

    public static int makeNegative(int n) {
        return n >= 0 ? -n : n;
    }

    public static float makePositive(float n) {
        return n >= 0 ? n : -n;
    }

    public static float makeNegative(float n) {
        return n >= 0 ? -n : n;
    }

    public static boolean isPositive(int n) {
        return n >= 0;
    }

    public static boolean isPositive(float n) {
        return n >= 0;
    }

    public static boolean isNegative(int n) {
        return n < 0;
    }

    public static boolean isNegative(float n) {
        return n < 0;
    }

    public static float increase(float value, float increase) {
        if (value > 0)
            value += increase;
        else if (value < 0)
            value -= increase;
        return value;
    }

    public static float decrease(float value, float decrease) {
        if (value > 0) {
            value -= decrease;
            if (value < 0) value = 0;
        } else if (value < 0) {
            value += decrease;
            if (value > 0) value = 0;
        }
        return value;
    }
}

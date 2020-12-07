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

import android.graphics.Color;
import androidx.core.graphics.ColorUtils;

public class ColorTools {
    public static int manipulateColor(int color, float manipulateValue) {
        try {
            float[] hsv = new float[3];
            Color.colorToHSV(color, hsv);
            hsv[2] = hsv[2] * manipulateValue;
            return Color.HSVToColor(hsv);
        } catch (Exception e) {
        }
        return color;
    }

    public static int manipulateAlphaChannel(int color, float ratio) {
        return manipulateColor(color, ratio, 1f, 1f, 1f);
    }

    public static int manipulateRedChannel(int color, float ratio) {
        return manipulateColor(color, 1f, ratio, 1f, 1f);
    }

    public static int manipulateGreenChannel(int color, float ratio) {
        return manipulateColor(color, 1f, 1f, ratio, 1f);
    }

    public static int manipulateBlueChannel(int color, float ratio) {
        return manipulateColor(color, 1f, 1f, 1f, ratio);
    }

    public static int manipulateColor(int color, float ratioAlpha, float ratioRed, float ratioGreen, float ratioBlue) {
        try {
            int alpha = Math.round(Color.alpha(color) * ratioAlpha);
            int red = Math.round(Color.red(color) * ratioRed);
            int green = Math.round(Color.green(color) * ratioGreen);
            int blue = Math.round(Color.blue(color) * ratioBlue);
            return Color.argb(alpha, red, green, blue);
        } catch (Exception e) {
        }
        return color;
    }

    public static float getLightness(int color) {
        try {
            float[] hsl = new float[3];
            ColorUtils.RGBToHSL(Color.red(color), Color.green(color), Color.blue(color), hsl);
            return hsl[2];
        } catch (Exception e) {
        }
        return 0f;
    }

    public static String colorToHexa(int color) {
        try {
            return String.format("#%08X", 0xFFFFFFFF & color);
        } catch (Exception e) {
        }
        return null;
    }
}
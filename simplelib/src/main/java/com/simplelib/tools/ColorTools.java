package com.simplelib.tools;

import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;

public class ColorTools {
    public static int manipulateColor(int color, float manipulateValue) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = hsv[2] * manipulateValue;
        return Color.HSVToColor(hsv);
    }

    public static float getLightness(int color) {
        float[] hsl = new float[3];
        ColorUtils.RGBToHSL(Color.red(color), Color.green(color), Color.blue(color), hsl);
        return hsl[2];
    }

    public static String colorToHexa(int color) {
        try {
            return String.format("#%08X", 0xFFFFFFFF & color);
        } catch (Exception e) {
        }
        return null;
    }
}
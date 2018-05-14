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
}

package com.simplelib.tools;

import com.simplelib.math.Vector2;

public class PointTools {
    public static double getDistance(Vector2 p1, Vector2 p2) {
        Vector2 d = getDistanceXY(p1, p2);

        return (double) Math.sqrt(Math.pow(d.x, 2) + Math.pow(d.y, 2));
    }

    public static int getDistanceAsInt(Vector2 p1, Vector2 p2) {
        Vector2 d = getDistanceXY(p1, p2);

        return (int) Math.sqrt(Math.pow(d.x, 2) + Math.pow(d.y, 2));
    }

    public static Vector2 getDistanceXY(Vector2 p1, Vector2 p2) {
        return new Vector2(getDistance(p1.x, p2.x), getDistance(p1.y, p2.y));
    }

    public static double getDistance(double p1, double p2) {
        return Math.max(p1, p2) - Math.min(p1, p2);
    }
}
